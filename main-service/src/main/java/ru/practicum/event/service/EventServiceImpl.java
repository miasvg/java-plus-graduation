package ru.practicum.event.service;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventSearchParam;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;
    LocationRepository locationRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;

    @Override
    public EventDtoPrivate addEvent(Long userId, NewEventRequest request) {
        log.info("Начинаем создание мероприятия {} пользователем id = {}", request, userId);
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        log.info("Получаем пользователя создателя мероприятия {}", initiator);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category", request.getCategoryId()));
        log.info("Получаем категорию для меропрития {}", category);
        Location location = locationRepository.save(LocationMapper.mapToLocationNew(request.getLocation()));
        log.info("Создаем локацию меропрития {}", location);
        Event create = EventMapper.mapToEventNew(request, category, location, initiator);
        Event event = eventRepository.save(create);
        log.info("Создание меропрития {} завершено", event);
        return EventMapper.mapToDtoPrivate(event);
    }

    @Override
    public Optional<EventDtoPrivate> getByIdPublic(Long eventId) {
        log.info("Начинаем поиск мероприятия id = {} со статусом Published", eventId);
        Optional<Event> eventOp = eventRepository.findByIdAndState(eventId, "PUBLISHED");
        if (eventOp.isPresent()) {
            Event event = eventOp.orElseThrow();
            updateViews(event);
            EventDtoPrivate eventDto = EventMapper.mapToDtoPrivate(event);
            log.info("Мероприятие найдено: {}", eventDto);
            return Optional.of(eventDto);
        } else {
            log.info("Мероприятие не найдено, возвращаем Optional.empty()");
            return Optional.empty();
        }
    }

    @Override
    public EventDtoPrivate getByIdPrivate(Long userId, Long eventId) {
        log.info("Получаем пользователя с id = {}", userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        log.info("Начинаем получение мероприятия id = {}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));
        updateViews(event);
        log.info("Мероприятие успешно получено: {}", event);
        return EventMapper.mapToDtoPrivate(event);
    }

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Pageable page) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Page<Event> events = eventRepository.findByInitiatorIdAndState(userId, "PUBLISHED", page);
        log.info("Получаем все опубликованные мероприятия для пользователя id = {}", userId);
        return events.getContent().stream()
                .map(EventMapper::mapToShortDto)
                .toList();
    }

    @Override
    public List<EventShortDto> getEventsWithParamAdmin(EventSearchParam eventSearchParam, Pageable page) {
        log.info("Начинаем получение событий с фильтрами для Admin API");
        Specification<Event> spec = createSpecification(eventSearchParam);
        Page<Event> events = eventRepository.findAll(spec, page);
        log.info("Список отфильрованных мероприятий для Admin API получен: {}", events);
        return events.getContent().stream()
                .map(EventMapper::mapToShortDto)
                .toList();
    }

    @Override
    public List<EventShortDto> getEventsWithParamPublic(EventSearchParam eventSearchParam, Pageable page) {
        log.info("Начинаем получение событий с фильтрами для Public API");
        Specification<Event> spec = createSpecification(eventSearchParam);
        Page<Event> events = eventRepository.findAll(spec, page);
        log.info("Список отфильрованных мероприятий для Public API получен: {}", events);
        List<Long> eventIds = events.getContent().stream()
                .map(Event::getId)
                .toList();
        log.info("Обновляем счетчик просмотров для списка id Event: {}", eventIds);
        eventRepository.incrementViews(eventIds);
        return events.getContent().stream()
                .map(EventMapper::mapToShortDto)
                .toList();
    }

    private void updateViews(Event event) {
        event.setViews(event.getViews() + 1);
        log.info("Обновляем счетчик просмотров для мероприятия id = {}", event.getId());
        eventRepository.save(event);
    }

    private Specification<Event> createSpecification(final EventSearchParam searchParam) {
        return (root, query, cb) -> {
            log.info("Начинаем фильтрацию переданных параметров");
            List<Predicate> predicates = new ArrayList<>();

            log.info("Проводим фильтрацию по пользователям");
            if (searchParam.getUsers() != null) {
                predicates.add(root.get("initiator").get("id").in(searchParam.getUsers()));
            }

            log.info("Проводим фильтрацию по состояниям");
            if (searchParam.getStates() != null) {
                predicates.add(root.get("state").in(searchParam.getStates()));
            }

            log.info("Проводим фильтрацию по категориям");
            if (searchParam.getCategories() != null) {
                predicates.add(root.get("category").get("id").in(searchParam.getCategories()));
            }

            log.info("Проводим фильтрацию по времени начала");
            if (searchParam.getRangeStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), searchParam.getRangeStart()));
            }

            log.info("Проводим фильтрацию по времени окончания");
            if (searchParam.getRangeEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), searchParam.getRangeEnd()));
            }

            log.info("Проводим текстовый поиск");
            if (searchParam.getText() != null) {
                String searchText = searchParam.getText().toLowerCase();

                log.info("Создание предиката для поиска по текстовому запросу в аннотации");
                Predicate annotationPredicate = cb.like(
                        cb.lower(root.get("annotation")),
                        "%" + searchText + "%"
                );

                log.info("Создание предиката для поиска по текстовому запросу в описании");
                Predicate descriptionPredicate = cb.like(
                        cb.lower(root.get("description")),
                        "%" + searchText + "%"
                );

                log.info("Объединяем предикаты для поиска в обоих полях");
                predicates.add(cb.or(annotationPredicate, descriptionPredicate));
            }
            log.info("Возвращаем лист с параметрами поиска");
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

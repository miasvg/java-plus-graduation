package ru.practicum.event.service;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.InvalidRequestException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Transactional
    @Override
    public EventDtoPrivate addEvent(Long userId, NewEventRequest request) {
        log.info("Начинаем создание мероприятия {} пользователем id = {}", request, userId);
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        log.info("Получаем пользователя создателя мероприятия {}", initiator);
        Category category = categoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Category", request.getCategory()));
        log.info("Получаем категорию для меропрития {}", category);
        Location location = locationRepository.save(LocationMapper.mapToLocationNew(request.getLocation()));
        log.info("Создаем локацию меропрития {}", location);
        Event create = EventMapper.mapToEventNew(request, category, location, initiator);
        Event event = eventRepository.save(create);
        log.info("Создание меропрития {} завершено", event);
        return EventMapper.mapToDtoPrivate(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest request) {
        Event event = getEvent(eventId);
        log.info("Валидация события (id {}) для обновления пользователем (id {})", event.getId(), userId);
        if (!(userRepository.existsById(userId) &&
                event.getInitiator().getId().equals(userId) &&
                !event.getState().equals(State.PUBLISHED))) {
            log.warn("Конфликт при запросе на обновление события");
            throw new ConflictException("Данное событие нельзя обновлять");
        }
        updateEventFields(event, request);

        return EventMapper.mapToFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest request) {
        Event event = getEvent(eventId);
        log.info("Валидация события (id {}) для обновления", event.getId());
        if (request.getStateAction() != null
                && request.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())
                && !event.getState().equals(State.PENDING)) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }
        if (request.getStateAction() != null
                && request.getStateAction().equals(StateAction.CANCEL_REVIEW.toString())
                && !event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано ");
        }
        updateEventFields(event, request);
        return EventMapper.mapToFullDto(eventRepository.save(event));
    }

    private Event getEvent(Long id) {
        log.info("Поиск мероприятия (id {})", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие", id));
    }

    private void updateEventFields(Event event, UpdateEventRequest request) {
        if (request.getAnnotation() != null) {
            String annotation = request.getAnnotation();
//            if (annotation.length() < 20 || annotation.length() > 2000) {
//                throw new InvalidRequestException("Длина аннотации не соответствует требованиям");
//            }
            log.debug("Обновление краткого описания события");
            event.setAnnotation(annotation);
        }
        if (request.getCategory() != null) {
            Long id = request.getCategory().longValue();
            event.setCategory(categoryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Категория", id)));
            log.debug("Обновление категории события");
        }
        if (request.getDescription() != null) {
            String desc = request.getDescription();
//            if (desc.length() < 20 || desc.length() > 7000) {
//                throw new InvalidRequestException("Длина описания не соответствует требованиям");
//            }
            log.debug("Обновление полного описания");
            event.setDescription(desc);
        }
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidRequestException("Событие не может начинаться раньше, чем через 2 часа");
            }
            log.debug("Обновление даты и времени события");
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            log.debug("Обновление места проведения события");
            Location newLocation = locationRepository.save(LocationMapper.mapToLocationNew(request.getLocation()));
            event.setLocation(newLocation);
        }
        if (request.getPaid() != null) {
            log.debug("Обновление поля необходимости оплаты события");
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            if (request.getParticipantLimit() < 0) {
                throw new InvalidRequestException("Лимит участников не может быть отрицательным");
            }
            log.debug("Обновление лимита участников события");
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            log.debug("Обновление статуса пре-модерации для события");
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getStateAction() != null) {
            log.debug("Обновление статуса события");
            switch (request.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(State.PENDING);
                    break;
                case "PUBLISH_EVENT":
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                default:
                    event.setState(State.CANCELED);
            }
        }
        if (request.getTitle() != null) {
            String title = request.getTitle();
//            if (title.length() < 3 || title.length() > 120) {
//                throw new InvalidRequestException("Длина заголовка не соответствует требованиям");
//            }
            log.debug("Обновление заголовка события");
            event.setTitle(title);
        }
    }

    @Transactional
    @Override
    public Optional<EventDtoPrivate> getByIdPublic(Long eventId) {
        log.info("Начинаем поиск мероприятия id = {} со статусом Published", eventId);
        Optional<Event> eventOp = eventRepository.findByIdAndState(eventId, State.PUBLISHED);
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

    @Transactional
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

    @Transactional
    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Pageable page) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Page<Event> events = eventRepository.findByInitiatorIdAndState(userId, State.PUBLISHED, page);
        log.info("Получаем все опубликованные мероприятия для пользователя id = {}", userId);
        return events.getContent().stream()
                .map(EventMapper::mapToShortDto)
                .toList();
    }

    @Transactional
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

    @Transactional
    @Override
    public List<EventShortDto> getEventsWithParamPublic(EventSearchParam eventSearchParam, Pageable page) {
        log.info("Начинаем получение событий с фильтрами для Public API");
        Specification<Event> spec = createSpecification(eventSearchParam);
        List<EventShortDto> events = eventRepository.findAll(spec, page).getContent().stream()
                .map(EventMapper::mapToShortDto)
                .toList();
        ;
        log.info("Список отфильрованных мероприятий для Public API получен: {}", events);
        List<Long> eventIds = events.stream()
                .map(EventShortDto::getId)
                .toList();
        if (!events.isEmpty()) {
            log.info("Обновляем счетчик просмотров для списка id Event: {}", eventIds);
            eventRepository.incrementViews(eventIds);
        }
        return events;
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

            log.info("Проводим фильтрацию по пользователям {}", searchParam.getUsers());
            if (searchParam.getUsers() != null && !searchParam.getUsers().isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(searchParam.getUsers()));
            }

            log.info("Проводим фильтрацию по состояниям {}", searchParam.getStates());
            if (searchParam.getStates() != null && !searchParam.getStates().isEmpty()) {
                predicates.add(root.get("state").in(searchParam.getStates()));
            }

            log.info("Проводим фильтрацию по категориям {}", searchParam.getCategories());
            if (searchParam.getCategories() != null && !searchParam.getCategories().isEmpty()) {
                predicates.add(root.get("category").get("id").in(searchParam.getCategories()));
            }

            log.info("Проводим фильтрацию по начальному времени диапазона выборки {}", searchParam.getRangeStart());
            if (searchParam.getRangeStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), searchParam.getRangeStart()));
            }

            log.info("Проводим фильтрацию по окончанию диапазона выборки {}", searchParam.getRangeEnd());
            if (searchParam.getRangeEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), searchParam.getRangeEnd()));
            }

            log.info("Установка диапазона выборки при нулевом значении rangeStart и нулевом значении rangeEnd");
            if (searchParam.getRangeStart() == null && searchParam.getRangeEnd() == null) {
                log.info("Диапазон начальной даты не указан, выборка проводится по предстоящим событиям");
                Expression<Timestamp> currentTime = cb.currentTimestamp();
                Expression<LocalDateTime> eventDate = root.get("eventDate");
                predicates.add(cb.greaterThan(eventDate,
                        cb.function("to_timestamp", LocalDateTime.class, currentTime)));
            }

            log.info("Установка диапазона выборки при нулевом значении rangeStart и указанном значении rangeEnd");
            if (searchParam.getRangeStart() == null && searchParam.getRangeEnd() != null) {
                log.info("Диапазон начальной и конечной даты не указан, выборка проводится по всем предстоящим событиям");
                Expression<Timestamp> currentTime = cb.currentTimestamp();
                Expression<LocalDateTime> eventDate = root.get("eventDate");
                predicates.add(cb.greaterThan(eventDate,
                        cb.function("to_timestamp", LocalDateTime.class, currentTime)));
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), searchParam.getRangeEnd()));
            }

            log.info("Проводим текстовый поиск по запросу {}", searchParam.getText());
            if (searchParam.getText() != null) {
                String searchText = searchParam.getText().toLowerCase();

                if (searchText.isEmpty() || searchText.isBlank()) {
                    log.info("Текстовый поиск не выполняется - строка пуста");
                } else if ("0".equals(searchText)) {
                    log.info("Обнаружено значение '0' в текстовом поиске");
                } else {
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
            }

            log.info("Добавляем сортировку по параметру {}", searchParam.getSort());
            if (searchParam.getSort() != null) {
                switch (searchParam.getSort().toUpperCase()) {
                    case "EVENT_DATE":
                        query.orderBy(cb.asc(root.get("eventDate")));
                        break;
                    case "VIEWS":
                        query.orderBy(cb.asc(root.get("views")));
                        break;
                }
            }

            log.info("Проводим поиск по paid {}", searchParam.getPaid());
            if (searchParam.getPaid() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paid"), searchParam.getPaid()));
            }

            log.info("Проводим поиск по onlyAvailable {}", searchParam.getOnlyAvailable());
            if (searchParam.getOnlyAvailable() != null) {
                Expression<Integer> limitExpr = root.get("participantLimit");
                Expression<Integer> requestsExpr = root.get("confirmedRequests");
                if (searchParam.getOnlyAvailable()) {
                    predicates.add(cb.lessThan(requestsExpr, limitExpr));
                } else {
                    predicates.add(cb.greaterThanOrEqualTo(requestsExpr, limitExpr));
                }
            }
            log.info("Количество собранных предикатов {}", predicates.size());
            log.info("Возвращаем лист с параметрами поиска");
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

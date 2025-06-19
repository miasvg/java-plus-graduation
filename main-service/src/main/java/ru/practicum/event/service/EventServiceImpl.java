package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventDtoPrivate;
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

import java.util.List;

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
    public EventDtoPrivate getById(Long eventId) {
        log.info("Начинаем получение мероприятия id = {}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event", eventId));
        event.setViews(event.getViews() + 1);
        log.info("Обновляем счетчик просмотров для мероприятия id = {}", eventId);
        eventRepository.save(event);
        log.info("Мероприятие успешно получено: {}", event);
        return EventMapper.mapToDtoPrivate(event);
    }

    @Override
    public List<EventDtoPrivate> getUsersEvents(Long userId, Pageable page) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        Page<Event> events = eventRepository.findByInitiator_idAndStatePublished(userId, page);
        log.info("Получаем все опубликованные мероприятия для пользователя id = {}", userId);
        return events.getContent().stream()
                .map(EventMapper::mapToDtoPrivate)
                .toList();
    }
}

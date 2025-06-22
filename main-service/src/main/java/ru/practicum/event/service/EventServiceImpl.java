package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

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


    //TODO
    // изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
    // дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие", eventId));
        if (!isEventUpdRequestValid(userId, event)) {
            throw new ConflictException("Данное событие нельзя обновлять");
        }
        return null;
    }

    private boolean isEventUpdRequestValid(Long userId, Event event) {
        return userRepository.existsById(userId) &&
                event.getInitiator().getId().equals(userId) &&
                !event.getState().equals(State.PUBLISHED);
    }
}

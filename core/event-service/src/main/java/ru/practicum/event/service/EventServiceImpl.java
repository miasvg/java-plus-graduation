package ru.practicum.event.service;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RecommendationsClient;
import ru.practicum.UserActionClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.enums.State;
import ru.practicum.enums.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.ViewsRepository;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.InvalidRequestException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.feign.FeignUserClient;
import ru.practicum.feign.FeingLikeRequestClient;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import stats.messages.analyzer.AnalyzerMessages;
import stats.messages.collector.UserAction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.enums.Status.CONFIRMED;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final FeignUserClient userClient;
    private final CategoryRepository categoryRepository;
    private final RecommendationsClient recommendationsClient;
    private final FeingLikeRequestClient likeClient;
    private final UserActionClient statClient;

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventRequest request) {
        log.info("Начинаем создание мероприятия {} пользователем id = {}", request, userId);
        UserDto initiator = userClient.getUserById(userId).orElseThrow(() -> new NotFoundException("User", userId));
        log.info("Получаем пользователя создателя мероприятия {}", initiator);
        Category category = categoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new NotFoundException("Category", request.getCategory()));
        log.info("Получаем категорию для меропрития {}", category);
        Location location = locationRepository.save(LocationMapper.mapToLocationNew(request.getLocation()));
        log.info("Создаем локацию меропрития {}", location);
        Event create = EventMapper.mapToEventNew(request, category, location, initiator);
        Event event = eventRepository.save(create);
        log.info("Создание меропрития {} завершено", event);
        return EventMapper.mapToFullDto(event, 0);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest request) {
        Event event = getEvent(eventId);
        log.info("Валидация события (id {}) для обновления пользователем (id {})", event.getId(), userId);
        if (!(userClient.getUserById(userId).isPresent() &&
                event.getInitiator().equals(userId) &&
                !event.getState().equals(State.PUBLISHED))) {
            log.warn("Конфликт при запросе на обновление события");
            throw new ConflictException("Данное событие нельзя обновлять");
        }
        updateEventFields(event, request);
        Optional<AnalyzerMessages.RecommendedEventProto> interactionsCount =
                recommendationsClient.getInteractionsCount(List.of(eventId)).findFirst();
        double rating = 0.0;
        if (interactionsCount.isPresent()) {
            rating = interactionsCount.get().getScore();
        }

        return EventMapper.mapToFullDto(eventRepository.save(event), rating);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest request) {
        Event event = getEvent(eventId);
        log.info("Валидация события (id {}) для обновления", event.getId());
        if (request.getStateAction() != null
                && request.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())
                && !event.getState().equals(State.PENDING)) {
            log.warn("Попытка публикации события, которое не в ожидании публикации");
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }
        if (request.getStateAction() != null
                && request.getStateAction().equals(StateAction.REJECT_EVENT.toString())
                && event.getState().equals(State.PUBLISHED)) {
            log.warn("Попытка отклонения события, которое уже опубликовано");
            throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано ");
        }
        updateEventFields(event, request);
        Optional<AnalyzerMessages.RecommendedEventProto> interactionsCount =
                recommendationsClient.getInteractionsCount(List.of(eventId)).findFirst();
        double rating = 0.0;
        if (interactionsCount.isPresent()) {
            rating = interactionsCount.get().getScore();
        }
        return EventMapper.mapToFullDto(eventRepository.save(event), rating);
    }

    @Transactional
    @Override
    public EventFullDto getByIdPrivate(Long userId, Long eventId, String ip) {
        log.info("Начинаем получение мероприятия id = {}", eventId);
        Event event = eventRepository.findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event", eventId));
        log.info("Мероприятие успешно получено: {}", event);
        Optional<AnalyzerMessages.RecommendedEventProto> interactionsCount =
                recommendationsClient.getInteractionsCount(List.of(eventId)).findFirst();
        double rating = 0.0;
        if (interactionsCount.isPresent()) {
            rating = interactionsCount.get().getScore();
        }
        return EventMapper.mapToFullDto(event, rating);
    }

    @Transactional
    @Override
    public EventFullDto getByIdPublic(Long eventId, String ip, Long userId) {
        log.info("Начинаем поиск мероприятия id = {} со статусом Published", eventId);
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event", eventId));
        log.info("Мероприятие найдено: {}", event);
        Optional<AnalyzerMessages.RecommendedEventProto> interactionsCount =
                recommendationsClient.getInteractionsCount(List.of(eventId)).findFirst();
        double rating = 0.0;
        if (interactionsCount.isPresent()) {
            rating = interactionsCount.get().getScore();
        }
        statClient.collectUserAction(eventId, userId, UserAction.ActionTypeProto.ACTION_VIEW, Instant.now());
        return EventMapper.mapToFullDto(event, rating);
    }

    @Transactional
    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Pageable page, String ip) {
        userClient.getUserById(userId).orElseThrow(() -> new NotFoundException("User", userId));

        Pageable sortedPage = PageRequest.of(
                page.getPageNumber(),
                page.getPageSize(),
                Sort.by("createdOn").descending().and(Sort.by("id").ascending())
        );

        Page<Event> events = eventRepository.findByInitiator(userId, sortedPage);
        log.info("Получаем все опубликованные мероприятия для пользователя id = {}: размер списка: {}, " +
                "список меропритий: {}", userId, events.getSize(), events.getContent());
        return events.stream().map(EventMapper::mapToShortDto).toList();
    }

    @Transactional
    @Override
    public List<EventShortDto> getEventsWithParamAdmin(EventSearchParam eventSearchParam, Pageable page) {
        log.info("Начинаем получение событий с фильтрами для Admin API");
        Specification<Event> spec = createSpecification(eventSearchParam);
        List<Event> events = eventRepository.findAll(spec, page);
        if (events.isEmpty()) {
            log.info("Не найдено событий для поиска Admin API с фильтрами");
        }
        log.info("Возвращаем список мероприятий для Admin API: {}", events);
        return events.stream().map(EventMapper::mapToShortDto).toList();
    }

    @Transactional
    @Override
    public List<EventShortDto> getEventsWithParamPublic(EventSearchParam eventSearchParam, Pageable page, String ip) {
        log.info("Начинаем получение событий с фильтрами для Public API");
        Specification<Event> spec = createSpecification(eventSearchParam);
        List<Event> eventsModel = eventRepository.findAll(spec, page).stream().toList();
        return eventsModel.stream().map(EventMapper::mapToShortDto).toList();
    }

    @Override
    public Optional<EventFullDto> getEventByIdFeign(Long id) {
        return eventRepository.findById(id).map(x -> EventMapper.mapToFullDto(x, 0.0));
    }

    @Override
    public Optional<EventFullDto> getEventByIdAndInitiator(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiator(eventId, userId).map(x -> EventMapper.mapToFullDto(x, 0.0));
    }

    @Override
    @Transactional
    public Boolean updateConfirmedRequests(Long eventId, Integer increment) {
        log.info("Мы обновляем Event с id {}, на {}", eventId, increment);
        eventRepository.incrementConfirmedRequests(eventId, increment);
        return true;
    }

    @Override
    public List<EventFullDto> getRecommendations(Long userId) {
        UserDto user = userClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        log.info("Определен пользователь комментатор {}", user);
        List<AnalyzerMessages.RecommendedEventProto> recommendations =
                recommendationsClient.getRecommendationsForUser(userId, 10)
                        .toList();

        if (recommendations.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> eventScores = recommendations.stream()
                .collect(Collectors.toMap(
                        AnalyzerMessages.RecommendedEventProto::getEventId,
                        AnalyzerMessages.RecommendedEventProto::getScore
                ));

        Map<Long, Event> eventMap = eventRepository.findAllById(eventScores.keySet())
                .stream()
                .collect(Collectors.toMap(Event::getId, Function.identity()));

        return recommendations.stream()
                .map(rec -> {
                    Event event = eventMap.get(rec.getEventId());
                    return event != null ? EventMapper.mapToFullDto(event, rec.getScore()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void setLike(Long userId, Long eventId) {
        Optional<Event> optEvent = eventRepository.findById(eventId);
        if (optEvent.isEmpty()) {
            throw new NotFoundException("Event  с Id " + eventId + " не найдено");
        }
        if (optEvent.get().getInitiator() == userId) {
            throw new InvalidRequestException("Нельзя поставить лайк своему событию");
        }
        UserDto user = userClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        Optional<EventRequestDto> optReq = likeClient.getByEventIdAndRequesterId(eventId, userId);
        if (optReq.isEmpty()) {
            throw new InvalidRequestException("Невозможно получить запрос на участие событии");
        }

        if (!optReq.get().getStatus().equals(CONFIRMED)) {
            throw new InvalidRequestException("Нельзя поставить лайк не участвуя в событии");
        }
        statClient.collectUserAction(eventId, userId, UserAction.ActionTypeProto.ACTION_LIKE, Instant.now());
        log.info("Лайк был поставлен пользователяем {} на событие {}", user, optEvent.get());

    }

    private Event getEvent(Long id) {
        log.info("Поиск мероприятия (id {})", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие", id));
    }

    private void updateEventFields(Event event, UpdateEventRequest request) {
        if (request.getAnnotation() != null) {
            log.debug("Обновление краткого описания события");
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Long id = request.getCategory().longValue();
            event.setCategory(categoryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Категория", id)));
            log.debug("Обновление категории события");
        }
        if (request.getDescription() != null) {
            log.debug("Обновление полного описания");
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.warn("Попытка публикации события c датой, не соответствующей требованиям: {}", request.getEventDate());
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
            log.debug("Обновление заголовка события");
            event.setTitle(request.getTitle());
        }
    }

    private Specification<Event> createSpecification(final EventSearchParam searchParam) {
        LocalDateTime rangeStart = searchParam.getRangeStart();
        LocalDateTime rangeEnd = searchParam.getRangeEnd();
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.error("Конец выборки находится во временной шкале раньше начала");
            throw new InvalidRequestException("Время окончания выборки не может быть раньше времени начала");
        }

        return (root, query, cb) -> {
            log.info("Начинаем фильтрацию переданных параметров");
            List<Predicate> predicates = new ArrayList<>();
            List<String> predicateLogs = new ArrayList<>();

            log.info("Проводим фильтрацию по пользователям {}", searchParam.getUsers());
            if (searchParam.getUsers() != null && !searchParam.getUsers().isEmpty()) {
                List<Long> validUsers = searchParam.getUsers().stream()
                        .filter(userId -> userId != null && userId > 0)
                        .collect(Collectors.toList());
                if (!validUsers.isEmpty()) {
                    predicateLogs.add("Пользователи: " + validUsers);
                    predicates.add(root.get("initiator").in(validUsers));
                }
            }


            log.info("Проводим фильтрацию по состояниям {}", searchParam.getStates());
            if (searchParam.getStates() != null && !searchParam.getStates().isEmpty()) {
                predicateLogs.add("Статусы: " + searchParam.getStates());
                predicates.add(root.get("state").in(searchParam.getStates()));
            }

            // Фильтр по категориям - игнорируем нулевые значения
            log.info("Проводим фильтрацию по категориям {}", searchParam.getCategories());
            if (searchParam.getCategories() != null && !searchParam.getCategories().isEmpty()) {
                List<Long> validCategories = searchParam.getCategories().stream()
                        .filter(catId -> catId != null && catId > 0)
                        .collect(Collectors.toList());
                if (!validCategories.isEmpty()) {
                    predicateLogs.add("Категории: " + validCategories);
                    predicates.add(root.get("category").get("id").in(validCategories));
                }
            }


            log.info("Проводим фильтрацию по временным рамкам: {}, {}", rangeStart, rangeEnd);
            if (rangeStart != null && rangeEnd != null) {
                log.info("Фильтрация по времени начала мероприятия, начальное время: {} конечное время {}",
                        rangeStart, rangeEnd);
                predicateLogs.add("Временной диапазон: от " + rangeStart + " до " + rangeEnd);
                predicates.add(cb.between(root.get("eventDate"), rangeStart, rangeEnd));
            } else {
                if (rangeStart != null) {
                    log.info("Время окончания выборки не указано, проводим фильтрацию по всем событиям от {}", rangeStart);
                    predicateLogs.add("Время начала выборки: " + rangeStart);
                    predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
                }

                if (rangeEnd != null) {
                    log.info("Время начала выборки не указано, проводим фильтрацию по всем событиям до {}", rangeEnd);
                    predicateLogs.add("Время окончания выборки: " + rangeEnd);
                    predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
                }

                if (rangeStart == null && rangeEnd == null) {
                    log.info("Диапазон не указан, выборка по всем предстоящим событиям");
                    predicateLogs.add("Время выборки не указано, выборка по предстоящим событиям от: " + LocalDateTime.now());
                    predicates.add(cb.greaterThan(root.get("eventDate"), cb.currentTimestamp()));
                }
            }


            log.info("Проводим текстовый поиск по запросу {}", searchParam.getText());
            if (searchParam.getText() != null && !searchParam.getText().trim().isEmpty()) {
                String searchText = searchParam.getText().toLowerCase().trim();

                Predicate annotationPredicate = cb.like(
                        cb.lower(root.get("annotation")),
                        "%" + searchText + "%"
                );

                Predicate descriptionPredicate = cb.like(
                        cb.lower(root.get("description")),
                        "%" + searchText + "%"
                );
                predicateLogs.add("Поиск по текстовому запросу: " + searchParam.getText());
                log.info("Объединяем предикаты для поиска в обоих полях");
                predicates.add(cb.or(annotationPredicate, descriptionPredicate));
            }

            log.info("Добавляем сортировку по параметру {}", searchParam.getSort());
            if (searchParam.getSort() != null) {
                switch (searchParam.getSort().toUpperCase()) {
                    case "EVENT_DATE":
                        predicateLogs.add("Поиск с сортировкой по: EVENT_DATE");
                        query.orderBy(cb.asc(root.get("eventDate")));
                        break;
                    case "VIEWS":
                        predicateLogs.add("Поиск с сортировкой по: VIEWS");
                        query.orderBy(cb.desc(root.get("views"))); // Обычно по убыванию
                        break;
                }
            }

            log.info("Проводим поиск по paid {}", searchParam.getPaid());
            if (searchParam.getPaid() != null) {
                predicateLogs.add("Поиск по paid с флагом: " + searchParam.getPaid());
                predicates.add(cb.equal(root.get("paid"), searchParam.getPaid())); // Используем equal вместо lessThanOrEqualTo
            }

            log.info("Проводим поиск по onlyAvailable {}", searchParam.getOnlyAvailable());
            if (searchParam.getOnlyAvailable() != null && searchParam.getOnlyAvailable()) {
                Predicate unlimitedEvents = cb.equal(root.get("participantLimit"), 0);
                Predicate availableEvents = cb.lessThan(root.get("confirmedRequests"), root.get("participantLimit"));

                predicateLogs.add("Поиск только доступных событий");
                predicates.add(cb.or(unlimitedEvents, availableEvents));
            }

            log.info("Количество собранных предикатов {}", predicates.size());
            log.info("Возвращаем лист с параметрами поиска: {}", predicateLogs);

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
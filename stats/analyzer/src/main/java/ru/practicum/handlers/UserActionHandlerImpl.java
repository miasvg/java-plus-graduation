package ru.practicum.handlers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.UserActionMapper;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    private final UserActionRepository userActionRepository;

    @Transactional
    @Override
    public void handle(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();
        double newMark = switch (action.getActionType()) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };

        if (!userActionRepository.existsByEventIdAndUserId(eventId, userId)) {
            userActionRepository.save(UserActionMapper.mapToUserAction(action));
            log.debug("Сохранено новое действие пользователя {}", action);
        } else {
            UserAction existing = userActionRepository.findByEventIdAndUserId(eventId, userId);
            if (existing.getMark() < newMark) {
                existing.setMark(newMark);
                existing.setTimestamp(action.getTimestamp());
                log.debug("Обновлено действие пользователя {}", existing);
            }
        }
    }
}

package ru.practicum.handlers;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionHandler {
    void handle(UserActionAvro action);
}

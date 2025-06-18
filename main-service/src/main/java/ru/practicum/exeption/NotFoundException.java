package ru.practicum.exeption;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String entityName, Long id) {
        super(String.format("%s с id %d не найден(а)", entityName, id));
    }
}

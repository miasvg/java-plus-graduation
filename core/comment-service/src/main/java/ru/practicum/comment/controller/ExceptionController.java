package ru.practicum.comment.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exeption.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ExceptionDto handleNotFound(final NotFoundException e) {
        log.error("Обрабатываем исключение NotFoundException");
        return ExceptionDto.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("resources not found")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleUpdateComment(final InvalidUpdateException e) {
        log.error("Обрабатываем исключение InvalidUpdateException");
        return ExceptionDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleForbiddenComment(final ForbiddenException e) {
        log.error("Обрабатываем исключение ForbiddenException");
        return ExceptionDto.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleCreateComment(final CreateCommentException e) {
        log.error("Обрабатываем исключение CreateCommentException");
        return ExceptionDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}

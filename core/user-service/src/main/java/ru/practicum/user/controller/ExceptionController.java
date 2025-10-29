package ru.practicum.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exeption.EmailMustBeUniqueException;
import ru.practicum.exeption.ExceptionDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto emailMustBeUniqueExceptionHandler(EmailMustBeUniqueException e) {
        log.error("Обрабатываем исключение EmailMustBeUniqueException");
        return ExceptionDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("email not unique")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto invalidDbRequestExceptionHandler(DataIntegrityViolationException e) {
        log.error("Обрабатываем исключение DataIntegrityViolationException");
        return ExceptionDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("request is invalid")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }




    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handlerValidEventDay(final MethodArgumentNotValidException e) {
        log.error("Обрабатываем исключение MethodArgumentNotValidException");
        return ExceptionDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("for the requested operation the conditions are not met")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }


}

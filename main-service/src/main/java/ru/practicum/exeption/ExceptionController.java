package ru.practicum.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ExceptionController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto emailMustBeUniqueExceptionHandler(EmailMustBeUniqueException e){
        return ExceptionDto.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("email not unique")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto userNotExistExceptionHandler(UserNotExistException e){
        return ExceptionDto.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("user not found")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}

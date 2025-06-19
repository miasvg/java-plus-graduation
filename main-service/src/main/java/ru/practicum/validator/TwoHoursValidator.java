package ru.practicum.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TwoHoursValidator implements ConstraintValidator<ValidEventDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime localDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursAgo = now.plusHours(2);

        return localDate.isAfter(twoHoursAgo) || localDate.isEqual(twoHoursAgo);
    }
}

package com.example.cinema_booking.utils;

import com.example.cinema_booking.validation.ValidDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }

        try {
            Field startDateJavaField = value.getClass().getDeclaredField(startDateField);
            startDateJavaField.setAccessible(true);
            LocalDate startDate = (LocalDate) startDateJavaField.get(value);

            Field endDateJavaField = value.getClass().getDeclaredField(endDateField);
            endDateJavaField.setAccessible(true);
            LocalDate endDate = (LocalDate) endDateJavaField.get(value);

            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(endDateField)
                        .addConstraintViolation();
                return false;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle reflection errors, e.g., log them or throw a specific exception
            return false;
        }
        return true;
    }
}

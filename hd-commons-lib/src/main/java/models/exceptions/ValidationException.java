package models.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class ValidationException extends StandardError {

    @Getter
    private List<FieldError> errors;

    public void addError(String fieldName, String message) {
        this.errors.add(new FieldError(fieldName, message));
    }

    @Getter
    @AllArgsConstructor
    private static class FieldError {
        private String fieldName;
        private String message;

    }
}

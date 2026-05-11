package example.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        List<ViolationResponse> violations = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new ViolationResponse(err.getField(), err.getDefaultMessage()))
                .distinct()
                .toList();
        log.warn("Validation failed (argument not valid). violationsCount={}", violations.size());
        return new ErrorResponse("Validation failed", violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        List<ViolationResponse> violations = e.getConstraintViolations().stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .map(v -> new ViolationResponse(v.getPropertyPath().toString(), v.getMessage()))
                .distinct()
                .toList();
        log.warn("Validation failed (constraint violation). violationsCount={}", violations.size());
        return new ErrorResponse("Validation failed", violations);
    }

}

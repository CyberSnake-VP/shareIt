package example.exception.handler;

import example.exception.BookingConflictException;
import example.exception.ConditionNotMetException;
import example.exception.NotAvailableException;
import example.exception.NotFoundException;
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
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConditionNotMetException(ConditionNotMetException e) {
        log.warn("Validation failed (condition not met). details={}", e.getMessage());
        return getErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn("Resource not found. details={}", e.getMessage());
        return new ErrorResponse(e.getMessage(), List.of());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailable(NotAvailableException e) {
        log.error("Validation failed: item not available. details={}", e.getMessage());
        return getErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingConflict(BookingConflictException e) {
        log.error("Validation failed:  booking date conflict. details={}", e.getMessage());
        return getErrorResponse(e);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerException(Exception e) {
        log.error("Server error. details={}", e.getMessage());
        return getErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Validation failed (argument not valid). details={}", e.getMessage());
        return getErrorResponse(e);
    }

    private ErrorResponse getErrorResponse(Throwable throwable) {
        return new ErrorResponse(throwable.getMessage(), List.of());
    }
}

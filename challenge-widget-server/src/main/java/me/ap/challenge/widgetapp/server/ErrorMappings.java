package me.ap.challenge.widgetapp.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Declarative Exception to HTTP error response mapper.
 * <p>
 * Creates a uniform and informative payload to use as an HTTP response for error states.
 * Hides details that, for security reasons, should not be part of an error response.
 */
@RestControllerAdvice
public class ErrorMappings {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorMappings.class);

    private ResponseEntity<ErrorResponse> obfuscate(Exception e,
                                                    HttpServletRequest request) {
        LOGGER.warn("Exception during request", e);
        return error("An internal server error occurred. Please miao at support if this issue persists.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    private ResponseEntity<ErrorResponse> error(Exception e,
                                                HttpStatus status,
                                                HttpServletRequest request) {
        LOGGER.info("Exception during request", e);
        return error(e.getMessage(), status, request);
    }

    private ResponseEntity<ErrorResponse> error(String message,
                                                HttpStatus status,
                                                HttpServletRequest request) {
        LOGGER.info("Exception during request: " + message);
        return ResponseEntity.status(status)
                .body(new ErrorResponse(ZonedDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e,
                                                HttpServletRequest request) {
        if (e.getCause() instanceof IllegalArgumentException) {
            return error(String.format("Parameter '%s' has the invalid value '%s'. Should be of type '%s'",
                            e.getPropertyName(),
                            e.getValue(),
                            Optional.ofNullable(e.getRequiredType()).map(type -> type.getName()).orElse("unspecified")),
                    BAD_REQUEST,
                    request);
        }

        return obfuscate(e, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getAllErrors().stream()
                .map(this::formatMessage)
                .collect(Collectors.joining(","));
        return error(message, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException e,
                                                HttpServletRequest request) {
        var cause = e.getCause();
        if (cause instanceof ValueInstantiationException causeException) {
            return handle(causeException, request);
        }

        if (cause instanceof MismatchedInputException causeException) {
            return handle(causeException, request);
        }

        if (cause instanceof JsonParseException causeException) {
            return error(causeException, BAD_REQUEST, request);
        }

        return error("Bad request", BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MismatchedInputException e,
                                                HttpServletRequest request) {
        var message = String.format("The structure or type of '%s' is invalid",
                e.getPath()
                        .stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .collect(Collectors.joining(".")));
        return error(message, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(ValueInstantiationException e,
                                                HttpServletRequest request) {
        var message = String.format("Field '%s' is invalid",
                e.getPath()
                        .stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .collect(Collectors.joining(".")));
        return error(message, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalArgumentException e,
                                                HttpServletRequest request) {
        return error(e, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(NoSuchElementException e,
                                                HttpServletRequest request) {
        return error(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e,
                                                HttpServletRequest request) {
        return error(e, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalStateException e,
                                                HttpServletRequest request) {
        return obfuscate(e, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MissingServletRequestPartException e,
                                                HttpServletRequest request) {
        return error(e, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(Exception e,
                                                HttpServletRequest request) {
        return obfuscate(e, request);
    }

    private String formatMessage(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            return String.format("field '%s' is invalid: %s", fieldError.getField(), fieldError.getRejectedValue());
        } else {
            return objectError.getDefaultMessage();
        }

    }

    public record ErrorResponse(ZonedDateTime timestamp, Integer status, String error, String message, String path) {
    }
}

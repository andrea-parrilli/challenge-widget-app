package ma.ap.challenge.widgetapp.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@RestControllerAdvice
public class ErrorMappings {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorMappings.class);

    private ResponseEntity<ErrorResponse> obfuscate(Exception e, HttpServletRequest request) {
        LOGGER.warn("Exception during request", e);
        return error(
                "An internal server error occurred. Please contact support if this issue persists.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorResponse> error(Exception e, HttpStatus status, HttpServletRequest request) {
        LOGGER.info("Exception during request", e);
        return error(e.getMessage(), status, request);
    }

    private ResponseEntity<ErrorResponse> error(String message, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        ZonedDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        if (e.getCause() instanceof IllegalArgumentException) {
            return error((IllegalArgumentException) e.getCause(),
                    BAD_REQUEST,
                    request
            );
        }

        return obfuscate(e, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) throws MethodArgumentNotValidException {
        // Spring default error block for this exception is good enough
        throw e;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException e, HttpServletRequest request) {
        if (e.getCause() instanceof ValueInstantiationException) {
            return handle((ValueInstantiationException) e.getCause(), request);
        }

        if (e.getCause() instanceof MismatchedInputException) {
            return handle((MismatchedInputException) e.getCause(), request);
        }

        if (e.getCause() instanceof JsonParseException) {
            return error((JsonParseException) e.getCause(), BAD_REQUEST, request);
        }

        return error("Bad request", BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MismatchedInputException e, HttpServletRequest request) {
        var message = String.format("The structure or type of '%s' is invalid", e.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining(".")));
        return error(message, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(ValueInstantiationException e, HttpServletRequest request) {
        var message = String.format("Field '%s' is invalid", e.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining(".")));
        return error(message, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalArgumentException e, HttpServletRequest request) {
        return error(e, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(NoSuchElementException e, HttpServletRequest request) {
        return error(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e, HttpServletRequest request) {
        return error(e, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(IllegalStateException e, HttpServletRequest request) {
        return obfuscate(e, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(MissingServletRequestPartException e, HttpServletRequest request) {
        return error(e, BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(Exception e, HttpServletRequest request) {
        return obfuscate(e, request);
    }

    @Getter
    public static class ErrorResponse {
        private final ZonedDateTime timestamp;
        private final Integer status;
        private final String error;
        private final String message;
        private final String path;

        public ErrorResponse(ZonedDateTime timestamp, Integer status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = StringUtils.isEmpty(message) ? error : message;
            this.path = path;
        }
    }
}

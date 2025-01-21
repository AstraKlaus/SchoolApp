package ak.spring.aop;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    // 1. Обработка ошибок валидации (из @Valid в контроллерах)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 2. Обработка ошибок валидации на уровне сервисов (из @Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 3. Обработка ошибок целостности данных (например, нарушение ограничения NOT NULL)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Нарушение целостности данных: " + extractErrorMessage(ex));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String extractErrorMessage(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        if (message.contains("not-null property")) {
            return "Обязательное поле отсутствует.";
        } else if (message.contains("unique constraint")) {
            return "Значение уже существует.";
        }
        return "Нарушение целостности данных.";
    }

    // 4. Обработка ошибок, связанных с отсутствующими параметрами запроса
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Отсутствует обязательный параметр запроса: " + ex.getParameterName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 5. Обработка ошибок метода запроса (например, использование POST вместо GET)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Метод запроса '" + ex.getMethod() + "' не поддерживается. Допустимые методы: " + ex.getSupportedHttpMethods());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 6. Обработка неподдерживаемого типа содержимого (например, отправка JSON вместо form-data)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Неподдерживаемый формат данных: " + ex.getContentType() + ". Поддерживаются: " + ex.getSupportedMediaTypes());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 7. Обработка ошибки доступа (например, недостаточно прав)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Доступ запрещен: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // 8. Обработка всех остальных исключений (непредусмотренные ошибки)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Произошла непредвиденная ошибка: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}




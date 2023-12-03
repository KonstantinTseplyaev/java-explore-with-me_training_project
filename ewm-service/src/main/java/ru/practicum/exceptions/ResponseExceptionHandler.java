package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler extends DefaultHandlerExceptionResolver {
    @ExceptionHandler(value = ModelNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleModelNotFoundExp(final ModelNotFoundException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при поиске", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = EventDateIncorrectException.class)
    public ResponseEntity<Map<String, String>> handleEventDateIncorrectExp(final EventDateIncorrectException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка при указании даты события",
                "errorMessage", exp.getMessage())));
    }

    @ExceptionHandler(value = RequestEventException.class)
    public ResponseEntity<Map<String, String>> handleERequestEventExp(final RequestEventException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(409).body((Map.of("error", "Ошибка при запросе на участие в событии",
                "errorMessage", exp.getMessage())));
    }

    @ExceptionHandler(value = EventStateException.class)
    public ResponseEntity<Map<String, String>> handleEventStateExp(final EventStateException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(409).body((Map.of("error", "Ошибка статуса события",
                "errorMessage", exp.getMessage())));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> duplicateDateExp(DataIntegrityViolationException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(409).body((Map.of("error", "Ошибка при указании нового email/name. " +
                        "Такое значение уже существует!",
                "errorMessage", exp.getMessage())));
    }

    @ExceptionHandler(value = ParamValidException.class)
    public ResponseEntity<Map<String, String>> handleParamValidExp(final ParamValidException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка при указании параметров",
                "errorMessage", exp.getMessage())));
    }
}

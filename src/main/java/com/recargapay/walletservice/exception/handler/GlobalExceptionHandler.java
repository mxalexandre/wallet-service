package com.recargapay.walletservice.exception.handler;

import com.recargapay.walletservice.exception.ApiException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
    log.error("method=handleApiException, status={}, message={}", ex.getStatus(), ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        ex.getStatus().value(),
        ex.getMessage()
    );
    return ResponseEntity.status(ex.getStatus()).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );
    log.error("method=handleValidationExceptions, exception={}, errors={}", ex, errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}

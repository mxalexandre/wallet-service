package com.recargapay.walletservice.exception.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.exception.ApiException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
    exceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleApiException_ShouldReturnErrorResponse() {
    ApiException apiException = new ApiException("Wallet not found", HttpStatus.NOT_FOUND);

    var response = exceptionHandler.handleApiException(apiException);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().getStatus());
    assertEquals("Wallet not found", response.getBody().getMessage());
  }

  @Test
  void handleValidationExceptions_ShouldReturnValidationErrors() {
    BindingResult bindingResult = mock(BindingResult.class);
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

    FieldError fieldError = new FieldError("walletRequest", "sourceWalletId", "Source wallet document is required");
    when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

    ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().containsKey("sourceWalletId"));
    assertEquals("Source wallet document is required", response.getBody().get("sourceWalletId"));
  }
}

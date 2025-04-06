package com.recargapay.walletservice.consumer;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.dto.TransactionMessage;
import com.recargapay.walletservice.service.TransactionService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionConsumerTest {

  private TransactionService transactionService;
  private MeterRegistry meterRegistry;
  private TransactionConsumer transactionConsumer;

  @BeforeEach
  void setUp() {
    transactionService = mock(TransactionService.class);
    meterRegistry = mock(MeterRegistry.class);

    transactionConsumer = new TransactionConsumer(transactionService, meterRegistry);
  }

  @Test
  void shouldConsumeMessageSuccessfully() {
    TransactionMessage message = TransactionMessage.builder()
        .transactionCode(java.util.UUID.randomUUID())
        .build();

    Counter counterSuccess = mock(Counter.class);
    when(meterRegistry.counter("transaction.messages.consumed.success")).thenReturn(counterSuccess);

    transactionConsumer.consume(message);

    verify(transactionService, times(1)).process(message);
    verify(counterSuccess, times(1)).increment();
    verify(meterRegistry, never()).counter("transaction.messages.consumed.failure");
  }

  @Test
  void shouldIncrementFailureCounterWhenExceptionThrown() {
    TransactionMessage message = TransactionMessage.builder()
        .transactionCode(java.util.UUID.randomUUID())
        .build();

    Counter counterFailure = mock(Counter.class);
    when(meterRegistry.counter("transaction.messages.consumed.failure")).thenReturn(counterFailure);

    doThrow(new RuntimeException("Processing error")).when(transactionService).process(message);

    try {
      transactionConsumer.consume(message);
    } catch (RuntimeException ex) {
    }

    verify(transactionService, times(1)).process(message);
    verify(counterFailure, times(1)).increment();
  }
}

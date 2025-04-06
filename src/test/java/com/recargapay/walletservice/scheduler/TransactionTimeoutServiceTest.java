package com.recargapay.walletservice.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.entity.Transaction;
import com.recargapay.walletservice.enums.TransactionStatus;
import com.recargapay.walletservice.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class TransactionTimeoutServiceTest {

  private TransactionRepository transactionRepository;
  private TransactionTimeoutService transactionTimeoutService;

  @BeforeEach
  void setUp() {
    transactionRepository = mock(TransactionRepository.class);
    transactionTimeoutService = new TransactionTimeoutService(transactionRepository);
  }

  @Test
  void shouldCancelExpiredPendingTransactions() {
    Transaction expiredTransaction = Transaction.builder()
        .transactionCode(UUID.randomUUID())
        .status(TransactionStatus.PENDING)
        .createdAt(LocalDateTime.now().minusMinutes(5))
        .build();

    when(transactionRepository.findByStatusAndCreatedAtBefore(
        eq(TransactionStatus.PENDING),
        any(LocalDateTime.class))
    ).thenReturn(List.of(expiredTransaction));

    transactionTimeoutService.cancelExpiredPendingTransactions();

    ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
    verify(transactionRepository).saveAll(captor.capture());

    List<Transaction> savedTransactions = captor.getValue();
    assertThat(savedTransactions).hasSize(1);

    Transaction saved = savedTransactions.get(0);
    assertThat(saved.getStatus()).isEqualTo(TransactionStatus.CANCELLED);
    assertThat(saved.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldDoNothingWhenNoExpiredTransactions() {
    when(transactionRepository.findByStatusAndCreatedAtBefore(
        eq(TransactionStatus.PENDING),
        any(LocalDateTime.class))
    ).thenReturn(Collections.emptyList());

    transactionTimeoutService.cancelExpiredPendingTransactions();

    verify(transactionRepository, times(1)).saveAll(Collections.emptyList());
  }
}

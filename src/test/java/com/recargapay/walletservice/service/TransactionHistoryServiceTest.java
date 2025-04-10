package com.recargapay.walletservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.recargapay.walletservice.entity.TransactionHistory;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.repository.TransactionHistoryRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

  @InjectMocks
  private TransactionHistoryService transactionHistoryService;

  @Mock
  private TransactionHistoryRepository transactionHistoryRepository;

  @Test
  void shouldRecordTransactionHistory() {
    Long walletId = 1L;
    BigDecimal amount = new BigDecimal("100.00");
    TransactionType type = TransactionType.DEPOSIT;
    String transactionCode = "tx-123";
    BigDecimal balanceBefore = new BigDecimal("500.00");
    BigDecimal balanceAfter = new BigDecimal("600.00");

    transactionHistoryService.recordHistory(walletId, amount, type, transactionCode, balanceBefore, balanceAfter);

    verify(transactionHistoryRepository).save(any(TransactionHistory.class));
  }
}

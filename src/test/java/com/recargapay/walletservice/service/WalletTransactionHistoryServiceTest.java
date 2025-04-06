package com.recargapay.walletservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.recargapay.walletservice.entity.WalletTransactionHistory;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.repository.WalletTransactionHistoryRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WalletTransactionHistoryServiceTest {

  @InjectMocks
  private WalletTransactionHistoryService walletTransactionHistoryService;

  @Mock
  private WalletTransactionHistoryRepository transactionHistoryRepository;

  @Test
  void shouldRecordTransactionHistory() {
    Long walletId = 1L;
    BigDecimal amount = new BigDecimal("100.00");
    TransactionType type = TransactionType.DEPOSIT;
    String transactionCode = "tx-123";
    BigDecimal balanceBefore = new BigDecimal("500.00");
    BigDecimal balanceAfter = new BigDecimal("600.00");

    walletTransactionHistoryService.recordHistory(walletId, amount, type, transactionCode, balanceBefore, balanceAfter);

    verify(transactionHistoryRepository).save(any(WalletTransactionHistory.class));
  }
}

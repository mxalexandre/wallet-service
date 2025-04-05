package com.recargapay.walletservice.service;

import com.recargapay.walletservice.entity.WalletTransactionHistory;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.repository.WalletTransactionHistoryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionHistoryService {

  private final WalletTransactionHistoryRepository transactionHistoryRepository;

  public void recordHistory(
      Long walletId,
      BigDecimal amount,
      TransactionType transactionType,
      String transactionCode,
      BigDecimal balanceBefore,
      BigDecimal balanceAfter
  ) {
    WalletTransactionHistory history = WalletTransactionHistory.builder()
        .walletId(walletId)
        .amount(amount)
        .type(transactionType)
        .transactionCode(transactionCode)
        .balanceBefore(balanceBefore)
        .balanceAfter(balanceAfter)
        .createdAt(LocalDateTime.now())
        .build();

    transactionHistoryRepository.save(history);
  }

}

package com.recargapay.walletservice.service;

import com.recargapay.walletservice.entity.TransactionHistory;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.repository.TransactionHistoryRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

  private final TransactionHistoryRepository transactionHistoryRepository;

  public void recordHistory(
      Long walletId,
      BigDecimal amount,
      TransactionType transactionType,
      String transactionCode,
      BigDecimal balanceBefore,
      BigDecimal balanceAfter
  ) {
    TransactionHistory history = TransactionHistory.builder()
        .walletId(walletId)
        .amount(amount)
        .type(transactionType)
        .transactionCode(transactionCode)
        .balanceBefore(balanceBefore)
        .balanceAfter(balanceAfter)
        .build();

    transactionHistoryRepository.save(history);
  }

}

package com.recargapay.walletservice.dto;

import com.recargapay.walletservice.entity.Transaction;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMessage {
  private UUID transactionCode;
  private Long sourceWalletId;
  private Long targetWalletId;
  private BigDecimal amount;
  private String type;

  public static TransactionMessage from(Transaction transaction) {
    return TransactionMessage.builder()
        .transactionCode(transaction.getTransactionCode())
        .sourceWalletId(transaction.getSourceWalletId())
        .targetWalletId(transaction.getTargetWalletId())
        .amount(transaction.getAmount())
        .type(transaction.getType().toString())
        .build();
  }
}

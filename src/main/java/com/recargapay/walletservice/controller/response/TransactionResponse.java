package com.recargapay.walletservice.controller.response;

import com.recargapay.walletservice.entity.Transaction;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

  private UUID transactionCode;
  private Long sourceWalletId;
  private Long targetWalletId;
  private BigDecimal amount;
  private String status;

  public static TransactionResponse from(Transaction transaction) {
    return TransactionResponse.builder()
        .transactionCode(transaction.getTransactionCode())
        .sourceWalletId(transaction.getSourceWalletId())
        .targetWalletId(transaction.getTargetWalletId())
        .amount(transaction.getAmount())
        .status(transaction.getStatus().toString())
        .build();
  }
}

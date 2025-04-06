package com.recargapay.walletservice.fixture;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.entity.Transaction;
import com.recargapay.walletservice.enums.TransactionStatus;
import com.recargapay.walletservice.enums.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public class TransactionFixture {

  public static Transaction buildTransaction() {
    return buildTransaction(TransactionType.DEPOSIT);
  }

  public static Transaction buildTransaction(TransactionType type) {
    return Transaction.builder()
        .transactionCode(UUID.randomUUID())
        .sourceWalletId(1L)
        .targetWalletId(2L)
        .amount(new BigDecimal("100.00"))
        .status(TransactionStatus.PENDING)
        .type(type)
        .build();
  }

  public static CreateTransactionRequest buildCreateTransactionRequest() {
    return CreateTransactionRequest.builder()
        .sourceWalletId(1L)
        .targetWalletId(2L)
        .amount(new BigDecimal("100.00"))
        .type(TransactionType.DEPOSIT)
        .build();
  }

}

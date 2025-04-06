package com.recargapay.walletservice.fixture;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.enums.TransactionType;
import java.math.BigDecimal;

public class CreateTransactionRequestFixture {

  public static CreateTransactionRequest deposit() {
    return CreateTransactionRequest.builder()
        .sourceWalletId(1L)
        .amount(new BigDecimal("100.00"))
        .type(TransactionType.DEPOSIT)
        .build();
  }

  public static CreateTransactionRequest withdraw() {
    return CreateTransactionRequest.builder()
        .sourceWalletId(1L)
        .amount(new BigDecimal("50.00"))
        .type(TransactionType.WITHDRAW)
        .build();
  }

  public static CreateTransactionRequest transfer() {
    return CreateTransactionRequest.builder()
        .sourceWalletId(1L)
        .targetWalletId(2L)
        .amount(new BigDecimal("30.00"))
        .type(TransactionType.TRANSFER)
        .build();
  }
}

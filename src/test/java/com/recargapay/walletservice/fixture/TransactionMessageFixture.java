package com.recargapay.walletservice.fixture;

import com.recargapay.walletservice.dto.TransactionMessage;
import com.recargapay.walletservice.enums.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public class TransactionMessageFixture {

  public static TransactionMessage get(TransactionType type) {
    return TransactionMessage.builder()
        .transactionCode(UUID.randomUUID())
        .sourceWalletId(1L)
        .targetWalletId(2L)
        .amount(new BigDecimal("100.00"))
        .type(type.name())
        .build();
  }

}

package com.recargapay.walletservice.fixture;

import com.recargapay.walletservice.entity.Wallet;
import java.math.BigDecimal;

public class WalletFixture {

  public static Wallet get() {
    return Wallet.builder()
        .id(1L)
        .ownerName("John Doe")
        .ownerDocument("12345678900")
        .balance(new BigDecimal("100.00"))
        .reservedAmount(BigDecimal.ZERO)
        .build();
  }


}

package com.recargapay.walletservice.controller.response;

import com.recargapay.walletservice.entity.Wallet;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {

  private Long id;
  private String ownerName;
  private String ownerDocument;
  private BigDecimal balance;

  public static WalletResponse from(final Wallet wallet) {
    return WalletResponse.builder()
        .id(wallet.getId())
        .ownerName(wallet.getOwnerName())
        .ownerDocument(wallet.getOwnerDocument())
        .balance(wallet.getBalance())
        .build();
  }

}

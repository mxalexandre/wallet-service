package com.recargapay.walletservice.controller.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDateResponse {

  private Long walletId;
  private BigDecimal balance;
  private String date;

}

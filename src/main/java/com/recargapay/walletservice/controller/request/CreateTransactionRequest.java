package com.recargapay.walletservice.controller.request;

import com.recargapay.walletservice.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequest {

  @NotNull(message = "Source wallet document is required")
  private Long sourceWalletId;

  private Long targetWalletId;

  @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least 0.01")
  @Digits(integer = 15, fraction = 2, message = "Amount must have at most two decimal places")
  private BigDecimal amount;

  @NotNull(message = "Transaction type is required")
  private TransactionType type;

}

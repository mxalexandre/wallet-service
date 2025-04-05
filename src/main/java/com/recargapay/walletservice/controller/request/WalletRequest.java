package com.recargapay.walletservice.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.recargapay.walletservice.util.StringSanitizerDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequest {

  @NotBlank(message = "Owner name is required.")
  @Size(min = 3, max = 140, message = "Owner name must be between 3 and 140 characters.")
  @JsonDeserialize(using = StringSanitizerDeserializer.class)
  private String ownerName;

  @NotBlank(message = "Owner document (CPF or CNPJ) is required")
  @Pattern(
      regexp = "^(\\d{11}|\\d{14})$",
      message = "Owner document must be a valid CPF (11 digits) or CNPJ (14 digits) without punctuation"
  )
  @JsonDeserialize(using = StringSanitizerDeserializer.class)
  private String ownerDocument;

}

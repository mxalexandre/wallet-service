package com.recargapay.walletservice.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.controller.response.TransactionResponse;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.service.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
@ExtendWith(SpringExtension.class)
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService transactionService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateTransaction() throws Exception {
    CreateTransactionRequest request = CreateTransactionRequest.builder()
        .sourceWalletId(1L)
        .targetWalletId(2L)
        .amount(BigDecimal.valueOf(50.00))
        .type(TransactionType.TRANSFER)
        .build();

    TransactionResponse response =TransactionResponse.builder()
        .transactionCode(UUID.randomUUID())
        .sourceWalletId(1L)
        .targetWalletId(2L)
        .amount(BigDecimal.valueOf(100.00))
        .status("PENDING")
        .build();


    when(transactionService.createTransaction(Mockito.any(CreateTransactionRequest.class)))
        .thenReturn(response);

    mockMvc.perform(post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldConfirmTransaction() throws Exception {
    UUID transactionCode = UUID.randomUUID();

    doNothing().when(transactionService).confirmTransaction(transactionCode);

    mockMvc.perform(post("/transactions/{transactionCode}/confirm", transactionCode))
        .andExpect(status().isAccepted());
  }
}

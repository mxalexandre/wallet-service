package com.recargapay.walletservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recargapay.walletservice.controller.request.WalletRequest;
import com.recargapay.walletservice.controller.response.BalanceDateResponse;
import com.recargapay.walletservice.controller.response.BalanceResponse;
import com.recargapay.walletservice.controller.response.WalletResponse;
import com.recargapay.walletservice.fixture.WalletFixture;
import com.recargapay.walletservice.service.WalletService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WalletController.class)
@ExtendWith(SpringExtension.class)
class WalletControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private WalletService walletService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateWallet() throws Exception {
    WalletResponse response = WalletResponse.from(WalletFixture.get());

    WalletRequest request = WalletRequest.builder()
        .ownerName("John Doe")
        .ownerDocument("12345678900")
        .build();

    when(walletService.createWallet(Mockito.any(WalletRequest.class))).thenReturn(response);

    mockMvc.perform(post("/wallets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldGetBalance() throws Exception {
    BalanceResponse response = new BalanceResponse(1L, new BigDecimal("100.00"));

    when(walletService.getBalance(1L)).thenReturn(response);

    mockMvc.perform(get("/wallets/1/balance"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldGetBalanceByDate() throws Exception {
    BalanceDateResponse response = new BalanceDateResponse(1L, new BigDecimal("150.00"), LocalDate.now().toString());

    when(walletService.getBalanceByDate(1L, LocalDate.parse("2025-04-06"))).thenReturn(response);

    mockMvc.perform(get("/wallets/1/balance-history")
            .param("date", "2025-04-06"))
        .andExpect(status().isOk());
  }
}

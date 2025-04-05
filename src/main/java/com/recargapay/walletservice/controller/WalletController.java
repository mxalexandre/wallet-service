package com.recargapay.walletservice.controller;

import com.recargapay.walletservice.controller.request.WalletRequest;
import com.recargapay.walletservice.controller.response.BalanceDateResponse;
import com.recargapay.walletservice.controller.response.BalanceResponse;
import com.recargapay.walletservice.controller.response.WalletResponse;
import com.recargapay.walletservice.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @Operation(
      summary = "Create Wallet",
      description = "Allow the creation of a new wallet."
  )
  @PostMapping
  public ResponseEntity<WalletResponse> createWallet(
      @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
      @Valid @RequestBody WalletRequest request) {
    log.info("method=createWallet, step=init, request={}, userAgent={}", request, userAgent);
    return  new ResponseEntity<>(
        walletService.createWallet(request),
        HttpStatus.CREATED
    );
  }

  @Operation(
      summary = "Get user balance",
      description = "Retrieve the user balance based on the id informed."
  )
  @GetMapping("/{id}/balance")
  public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long id) {
    log.info("method=getBalance, step=init, id={}", id);
    BalanceResponse balance = walletService.getBalance(id);
    return ResponseEntity.ok(balance);
  }

  @GetMapping("/{walletId}/balance-history")
  public ResponseEntity<BalanceDateResponse> getBalance(
      @PathVariable Long walletId,
      @RequestParam(value = "date", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    return ResponseEntity.ok(walletService.getBalanceByDate(walletId, date));
  }

}

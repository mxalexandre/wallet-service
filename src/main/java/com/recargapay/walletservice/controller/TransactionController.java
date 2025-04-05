package com.recargapay.walletservice.controller;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.controller.response.TransactionResponse;
import com.recargapay.walletservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @Operation(
      summary = "Create transaction",
      description = "Allow the creation of a new transaction."
  )
  @PostMapping
  public ResponseEntity<TransactionResponse> createTransaction(
      @RequestBody @Valid CreateTransactionRequest request) {
    log.info("method=createTransaction, step=init, transactionRequest: {}", request);
    TransactionResponse response = transactionService.createTransaction(request);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Confirm transaction",
      description = "Confirm a transaction previous created."
  )
  @PostMapping("/{transactionCode}/confirm")
  public ResponseEntity<Void> confirmTransaction(@PathVariable UUID transactionCode) {
    log.info("method=confirmTransaction, step=init, transactionCode: {}", transactionCode);
    transactionService.confirmTransaction(transactionCode);
    return ResponseEntity.accepted().build();
  }

}

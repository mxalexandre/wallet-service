package com.recargapay.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.controller.response.TransactionResponse;
import com.recargapay.walletservice.dto.TransactionMessage;
import com.recargapay.walletservice.entity.Transaction;
import com.recargapay.walletservice.enums.TransactionStatus;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.exception.ApiException;
import com.recargapay.walletservice.fixture.TransactionFixture;
import com.recargapay.walletservice.fixture.TransactionMessageFixture;
import com.recargapay.walletservice.producer.TransactionProducer;
import com.recargapay.walletservice.repository.TransactionRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private WalletService walletService;

  @Mock
  private TransactionProducer transactionProducer;

  @Mock
  private TransactionValidationService validationService;

  @Mock
  private WalletServiceHelper walletServiceHelper;

  @InjectMocks
  private TransactionService transactionService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransaction_shouldSaveTransactionAndReturnResponse() {
    CreateTransactionRequest request = TransactionFixture.buildCreateTransactionRequest();
    Transaction transaction = TransactionFixture.buildTransaction();

    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    TransactionResponse response = transactionService.createTransaction(request);

    assertNotNull(response);
    assertEquals(transaction.getTransactionCode(), response.getTransactionCode());
    verify(validationService).validateTransactionRequest(request);
    verify(transactionRepository).save(any(Transaction.class));
  }

  @Test
  void confirmTransaction_shouldReserveBalanceAndSendMessage() {
    Transaction transaction = TransactionFixture.buildTransaction(TransactionType.TRANSFER);
    when(transactionRepository.findByTransactionCodeAndStatus(any(), eq(TransactionStatus.PENDING)))
        .thenReturn(Optional.of(transaction));
    when(validationService.isWithdrawOrTransfer(transaction.getType()))
        .thenReturn(true);

    UUID transactionCode = transaction.getTransactionCode();

    transactionService.confirmTransaction(transactionCode);

    verify(walletService).reserveBalance(any(), any());
    verify(transactionProducer).send(any(TransactionMessage.class));
    assertEquals(TransactionStatus.PROCESSING, transaction.getStatus());
  }

  @Test
  void confirmTransaction_shouldThrowWhenTransactionNotFound() {
    UUID transactionCode = UUID.randomUUID();
    when(transactionRepository.findByTransactionCodeAndStatus(any(), eq(TransactionStatus.PENDING)))
        .thenReturn(Optional.empty());

    ApiException exception = assertThrows(ApiException.class, () ->
        transactionService.confirmTransaction(transactionCode)
    );

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  void processDeposit_shouldUpdateWalletBalanceAndCompleteTransaction() {
    Transaction transaction = TransactionFixture.buildTransaction(TransactionType.DEPOSIT);
    TransactionMessage message = TransactionMessageFixture.get(TransactionType.DEPOSIT);

    when(transactionRepository.findByTransactionCode(message.getTransactionCode()))
        .thenReturn(Optional.of(transaction));

    transactionService.process(message);

    verify(walletServiceHelper).updateWalletBalance(
        message.getSourceWalletId(),
        message.getAmount(),
        TransactionType.DEPOSIT,
        message.getTransactionCode().toString()
    );

    assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    verify(transactionRepository).save(transaction);
  }

  @Test
  void processWithdraw_shouldUpdateWalletBalanceAndCompleteTransaction() {
    Transaction transaction = TransactionFixture.buildTransaction(TransactionType.WITHDRAW);
    TransactionMessage message = TransactionMessageFixture.get(TransactionType.WITHDRAW);

    when(transactionRepository.findByTransactionCode(message.getTransactionCode()))
        .thenReturn(Optional.of(transaction));

    transactionService.process(message);

    verify(walletServiceHelper).updateWalletBalance(
        message.getSourceWalletId(),
        message.getAmount().negate(),
        TransactionType.WITHDRAW,
        message.getTransactionCode().toString()
    );

    assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    verify(transactionRepository).save(transaction);
  }

  @Test
  void processTransfer_shouldUpdateWalletBalanceForBothWallets() {
    Transaction transaction = TransactionFixture.buildTransaction(TransactionType.TRANSFER);
    TransactionMessage message = TransactionMessageFixture.get(TransactionType.TRANSFER);

    when(transactionRepository.findByTransactionCode(message.getTransactionCode()))
        .thenReturn(Optional.of(transaction));

    transactionService.process(message);

    verify(walletServiceHelper).updateWalletBalance(
        message.getSourceWalletId(),
        message.getAmount().negate(),
        TransactionType.TRANSFER,
        message.getTransactionCode().toString()
    );

    verify(walletServiceHelper).updateWalletBalance(
        message.getTargetWalletId(),
        message.getAmount(),
        TransactionType.TRANSFER,
        message.getTransactionCode().toString()
    );

    assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    verify(transactionRepository).save(transaction);
  }
}

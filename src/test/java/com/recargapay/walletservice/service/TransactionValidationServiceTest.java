package com.recargapay.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.entity.Wallet;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.fixture.CreateTransactionRequestFixture;
import com.recargapay.walletservice.fixture.WalletFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionValidationServiceTest {

  @InjectMocks
  private TransactionValidationService transactionValidationService;

  @Mock
  private WalletService walletService;

  @Test
  void shouldValidateDepositTransaction() {
    CreateTransactionRequest request = CreateTransactionRequestFixture.deposit();

    transactionValidationService.validateTransactionRequest(request);

    verify(walletService).validateWalletExists(request.getSourceWalletId());
    verify(walletService, never()).validateWalletExists(request.getTargetWalletId());
    verify(walletService, never()).checkBalance(any(), any());
  }

  @Test
  void shouldValidateWithdrawTransaction() {
    CreateTransactionRequest request = CreateTransactionRequestFixture.withdraw();
    Wallet wallet = WalletFixture.get();

    when(walletService.getWalletById(request.getSourceWalletId())).thenReturn(wallet);

    transactionValidationService.validateTransactionRequest(request);

    verify(walletService).validateWalletExists(request.getSourceWalletId());
    verify(walletService, never()).validateWalletExists(request.getTargetWalletId());
    verify(walletService).checkBalance(wallet, request.getAmount());
  }

  @Test
  void shouldValidateTransferTransaction() {
    CreateTransactionRequest request = CreateTransactionRequestFixture.transfer();
    Wallet wallet = WalletFixture.get();

    when(walletService.getWalletById(request.getSourceWalletId())).thenReturn(wallet);

    transactionValidationService.validateTransactionRequest(request);

    verify(walletService).validateWalletExists(request.getSourceWalletId());
    verify(walletService).validateWalletExists(request.getTargetWalletId());
    verify(walletService).checkBalance(wallet, request.getAmount());
  }

  @Test
  void isWithdrawOrTransfer_shouldReturnCorrectly() {
    assertTrue(transactionValidationService.isWithdrawOrTransfer(TransactionType.WITHDRAW));
    assertTrue(transactionValidationService.isWithdrawOrTransfer(TransactionType.TRANSFER));
    assertFalse(transactionValidationService.isWithdrawOrTransfer(TransactionType.DEPOSIT));
  }
}

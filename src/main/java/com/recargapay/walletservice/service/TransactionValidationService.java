package com.recargapay.walletservice.service;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.entity.Wallet;
import com.recargapay.walletservice.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionValidationService {

  private final WalletService walletService;

  public void validateTransactionRequest(CreateTransactionRequest request) {
    walletService.validateWalletExists(request.getSourceWalletId());

    if (TransactionType.TRANSFER.equals(request.getType())) {
      walletService.validateWalletExists(request.getTargetWalletId());
    }

    if (isWithdrawOrTransfer(request.getType())) {
      Wallet wallet = walletService.getWalletById(request.getSourceWalletId());
      walletService.checkBalance(wallet, request.getAmount());
    }
  }

  public boolean isWithdrawOrTransfer(TransactionType type) {
    return TransactionType.WITHDRAW.equals(type) || TransactionType.TRANSFER.equals(type);
  }

}

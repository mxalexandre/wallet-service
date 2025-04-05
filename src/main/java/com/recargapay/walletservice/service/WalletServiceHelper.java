package com.recargapay.walletservice.service;

import com.recargapay.walletservice.entity.Wallet;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceHelper {

  private final WalletRepository walletRepository;
  private final WalletTransactionHistoryService transactionHistoryService;

  public void updateWalletBalance(Long walletId, BigDecimal amount, TransactionType type, String transactionCode) {
    Wallet wallet = walletRepository.findById(walletId)
        .orElseThrow(() -> new RuntimeException("Wallet not found"));

    BigDecimal balanceBefore = Optional.ofNullable(wallet.getBalance()).orElse(BigDecimal.ZERO);
    BigDecimal balanceAfter = balanceBefore.add(amount);

    if ((TransactionType.WITHDRAW.equals(type) || TransactionType.TRANSFER.equals(type)) && amount.signum() < 0) {
      BigDecimal reservedBefore = Optional.ofNullable(wallet.getReservedAmount()).orElse(BigDecimal.ZERO);
      BigDecimal reservedAfter = reservedBefore.subtract(amount.abs());
      wallet.setReservedAmount(reservedAfter.max(BigDecimal.ZERO));
    }

    wallet.setBalance(balanceAfter);
    walletRepository.save(wallet);

    transactionHistoryService.recordHistory(
        wallet.getId(),
        amount,
        type,
        transactionCode,
        balanceBefore,
        balanceAfter
    );
  }

}

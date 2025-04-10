package com.recargapay.walletservice.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.entity.Wallet;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.fixture.WalletFixture;
import com.recargapay.walletservice.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WalletServiceHelperTest {

  @InjectMocks
  private WalletServiceHelper walletServiceHelper;

  @Mock
  private WalletRepository walletRepository;

  @Mock
  private TransactionHistoryService transactionHistoryService;

  private Wallet wallet;

  @BeforeEach
  void setUp() {
    wallet = WalletFixture.get();
  }

  @Test
  void shouldUpdateBalanceForDeposit() {
    when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

    walletServiceHelper.updateWalletBalance(wallet.getId(), new BigDecimal("100.00"), TransactionType.DEPOSIT, "tx-123");

    verify(walletRepository).save(wallet);
    verify(transactionHistoryService).recordHistory(
        eq(wallet.getId()),
        eq(new BigDecimal("100.00")),
        eq(TransactionType.DEPOSIT),
        eq("tx-123"),
        any(BigDecimal.class),
        any(BigDecimal.class)
    );
  }

  @Test
  void shouldUpdateBalanceAndReservedAmountForWithdraw() {
    when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

    wallet.setReservedAmount(new BigDecimal("50.00"));
    walletServiceHelper.updateWalletBalance(wallet.getId(), new BigDecimal("-30.00"), TransactionType.WITHDRAW, "tx-456");

    verify(walletRepository).save(wallet);
    verify(transactionHistoryService).recordHistory(
        eq(wallet.getId()),
        eq(new BigDecimal("-30.00")),
        eq(TransactionType.WITHDRAW),
        eq("tx-456"),
        any(BigDecimal.class),
        any(BigDecimal.class)
    );
  }

  @Test
  void shouldUpdateBalanceAndReservedAmountForTransfer() {
    when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

    wallet.setReservedAmount(new BigDecimal("50.00"));
    walletServiceHelper.updateWalletBalance(wallet.getId(), new BigDecimal("-20.00"), TransactionType.TRANSFER, "tx-789");

    verify(walletRepository).save(wallet);
    verify(transactionHistoryService).recordHistory(
        eq(wallet.getId()),
        eq(new BigDecimal("-20.00")),
        eq(TransactionType.TRANSFER),
        eq("tx-789"),
        any(BigDecimal.class),
        any(BigDecimal.class)
    );
  }

  @Test
  void shouldThrowExceptionWhenWalletNotFound() {
    when(walletRepository.findById(anyLong())).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class,
        () -> walletServiceHelper.updateWalletBalance(999L, BigDecimal.TEN, TransactionType.DEPOSIT, "tx-000")
    );
  }
}

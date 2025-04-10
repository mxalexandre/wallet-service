package com.recargapay.walletservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recargapay.walletservice.controller.request.WalletRequest;
import com.recargapay.walletservice.entity.Wallet;
import com.recargapay.walletservice.exception.ApiException;
import com.recargapay.walletservice.exception.WalletAlreadyExistsException;
import com.recargapay.walletservice.fixture.WalletFixture;
import com.recargapay.walletservice.repository.WalletRepository;
import com.recargapay.walletservice.repository.TransactionHistoryRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletServiceTest {

  private WalletService walletService;
  private WalletRepository walletRepository;
  private TransactionHistoryRepository historyRepository;

  @BeforeEach
  void setUp() {
    walletRepository = mock(WalletRepository.class);
    historyRepository = mock(TransactionHistoryRepository.class);
    walletService = new WalletService(walletRepository, historyRepository);
  }

  @Test
  void shouldCreateWalletSuccessfully() {
    WalletRequest request = new WalletRequest("John Doe", "12345678900");
    Wallet wallet = WalletFixture.get();

    when(walletRepository.existsByOwnerDocument(request.getOwnerDocument())).thenReturn(false);
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

    var response = walletService.createWallet(request);

    assertThat(response.getOwnerName()).isEqualTo("John Doe");
    assertThat(response.getOwnerDocument()).isEqualTo("12345678900");
    verify(walletRepository).save(any(Wallet.class));
  }

  @Test
  void shouldThrowExceptionWhenWalletAlreadyExists() {
    WalletRequest request = new WalletRequest("John Doe", "12345678900");

    when(walletRepository.existsByOwnerDocument(request.getOwnerDocument())).thenReturn(true);

    assertThatThrownBy(() -> walletService.createWallet(request))
        .isInstanceOf(WalletAlreadyExistsException.class);
  }

  @Test
  void shouldGetBalanceSuccessfully() {
    Wallet wallet = WalletFixture.get();

    when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

    var response = walletService.getBalance(wallet.getId());

    assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
  }

  @Test
  void shouldThrowExceptionWhenWalletNotFoundOnGetBalance() {
    when(walletRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> walletService.getBalance(1L))
        .isInstanceOf(ApiException.class);
  }

  @Test
  void shouldReserveBalanceSuccessfully() {
    Wallet wallet = WalletFixture.get();

    when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

    walletService.reserveBalance(wallet.getId(), new BigDecimal("50.00"));

    verify(walletRepository).save(any(Wallet.class));
  }

  @Test
  void shouldThrowExceptionWhenInsufficientBalance() {
    Wallet wallet = WalletFixture.get();
    wallet.setBalance(new BigDecimal("20.00"));

    when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

    assertThatThrownBy(() -> walletService.reserveBalance(wallet.getId(), new BigDecimal("50.00")))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("Insufficient available balance");
  }

  @Test
  void shouldValidateWalletExistsSuccessfully() {
    when(walletRepository.existsById(1L)).thenReturn(true);

    walletService.validateWalletExists(1L);

    verify(walletRepository).existsById(1L);
  }

  @Test
  void shouldThrowExceptionWhenWalletDoesNotExist() {
    when(walletRepository.existsById(1L)).thenReturn(false);

    assertThatThrownBy(() -> walletService.validateWalletExists(1L))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("Wallet not found for id");
  }

}
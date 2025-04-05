package com.recargapay.walletservice.service;

import com.recargapay.walletservice.controller.request.WalletRequest;
import com.recargapay.walletservice.controller.response.BalanceDateResponse;
import com.recargapay.walletservice.controller.response.BalanceResponse;
import com.recargapay.walletservice.controller.response.WalletResponse;
import com.recargapay.walletservice.entity.Wallet;
import com.recargapay.walletservice.exception.ApiException;
import com.recargapay.walletservice.exception.WalletAlreadyExistsException;
import com.recargapay.walletservice.repository.WalletRepository;
import com.recargapay.walletservice.repository.WalletTransactionHistoryRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class WalletService {

  private final WalletRepository walletRepository;
  private final WalletTransactionHistoryRepository historyRepository;

  @Transactional
  public WalletResponse createWallet(@Valid WalletRequest request) {
    log.info("method=createWallet, step=init, request={}", request);
    validateWalletDoesNotExist(request.getOwnerDocument());

    Wallet wallet = Wallet.builder()
        .ownerName(request.getOwnerName())
        .ownerDocument(request.getOwnerDocument())
        .balance(BigDecimal.ZERO)
        .build();
    var response = walletRepository.save(wallet);
    log.info("method=createWallet, step=finish, response={}", response);
    return WalletResponse.from(response);
  }

  @Cacheable(value = "walletBalanceCache", key = "#id")
  public BalanceResponse getBalance(Long id) {
    log.info("method=getBalance, step=init, id={}", id);
    Wallet wallet = walletRepository.findById(id)
        .orElseThrow(() -> new ApiException("Wallet not found", HttpStatus.NOT_FOUND));
    return BalanceResponse.builder()
        .walletId(wallet.getId())
        .balance(wallet.getBalance())
        .build();
  }

  @Transactional
  public void reserveBalance(Long walletId, BigDecimal amount) {
    log.info("m=reserveBalance, step=init, walletId={}, amount={}", walletId, amount);
    Wallet wallet = walletRepository.findById(walletId)
        .orElseThrow(() -> new ApiException("Wallet not found", HttpStatus.NOT_FOUND));

    checkBalance(wallet, amount);

    BigDecimal reservedAmount = Optional.ofNullable(wallet.getReservedAmount())
        .orElse(BigDecimal.ZERO);
    wallet.setReservedAmount(reservedAmount.add(amount));

    walletRepository.save(wallet);
  }

  public Wallet getWalletById(Long id) {
    return walletRepository.findById(id).orElseThrow(() -> new RuntimeException("Wallet not found"));
  }

  public void validateWalletExists(Long id) {
    boolean exists = walletRepository.existsById(id);

    if (!exists) {
      throw new ApiException("Wallet not found for id: " + id, HttpStatus.NOT_FOUND);
    }
  }

  private void validateWalletDoesNotExist(String ownerDocument) {
    if (walletRepository.existsByOwnerDocument(ownerDocument)) {
      throw new WalletAlreadyExistsException(ownerDocument);
    }
  }

  public void checkBalance(Wallet wallet, BigDecimal amount) {
    BigDecimal balance = Optional.ofNullable(wallet.getBalance()).orElse(BigDecimal.ZERO);
    BigDecimal reserved = Optional.ofNullable(wallet.getReservedAmount())
        .orElse(BigDecimal.ZERO);
    BigDecimal availableBalance = balance.subtract(reserved);

    if (availableBalance.compareTo(amount) < 0) {
      throw new ApiException("Insufficient available balance for the transaction.",
          HttpStatus.BAD_REQUEST);
    }
  }

  public BalanceDateResponse getBalanceByDate(Long walletId, LocalDate date) {
    log.info("method=getBalance, step=init, walletId={}, date={}", walletId, date);

    if (!date.isBefore(LocalDate.now())) {
      Wallet wallet = walletRepository.findById(walletId)
          .orElseThrow(() -> new ApiException("Wallet not found", HttpStatus.NOT_FOUND));

      return BalanceDateResponse.builder()
          .walletId(wallet.getId())
          .balance(wallet.getBalance())
          .date(date.toString())
          .build();
    }

    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    return historyRepository.findFirstByWalletIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            walletId, startOfDay, endOfDay)
        .map(history -> BalanceDateResponse.builder()
            .walletId(walletId)
            .balance(history.getBalanceAfter())
            .date(date.toString())
            .build())
        .orElseThrow(() -> new ApiException("No balance history found for the given date", HttpStatus.NOT_FOUND));
  }

}

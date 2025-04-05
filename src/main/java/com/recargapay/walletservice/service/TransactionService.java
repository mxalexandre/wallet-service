package com.recargapay.walletservice.service;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.controller.response.TransactionResponse;
import com.recargapay.walletservice.dto.TransactionMessage;
import com.recargapay.walletservice.entity.Transaction;
import com.recargapay.walletservice.enums.TransactionStatus;
import com.recargapay.walletservice.enums.TransactionType;
import com.recargapay.walletservice.exception.ApiException;
import com.recargapay.walletservice.producer.TransactionProducer;
import com.recargapay.walletservice.repository.TransactionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final WalletService walletService;
  private final TransactionProducer transactionProducer;
  private final TransactionValidationService validationService;
  private final WalletServiceHelper walletServiceHelper;

  @Transactional
  public TransactionResponse createTransaction(CreateTransactionRequest request) {
    validationService.validateTransactionRequest(request);
    Transaction transaction = transactionRepository.save(Transaction.from(request));
    log.info("m=createTransaction, step=finish, Created transaction with code: {}", transaction.getTransactionCode());
    return TransactionResponse.from(transaction);
  }

  @Transactional
  public void confirmTransaction(UUID transactionCode) {
    Transaction transaction = findPendingTransaction(transactionCode);
    
    if (validationService.isWithdrawOrTransfer(transaction.getType())) {
      walletService.reserveBalance(transaction.getSourceWalletId(), transaction.getAmount());
    }
    
    transaction.setStatus(TransactionStatus.PROCESSING);
    transactionRepository.save(transaction);

    log.info("Transaction {} confirmed. Sending to queue...", transactionCode);

    transactionProducer.send(TransactionMessage.from(transaction));
  }

  private Transaction findPendingTransaction(UUID transactionCode) {
    return transactionRepository.findByTransactionCodeAndStatus(transactionCode, TransactionStatus.PENDING)
        .orElseThrow(() -> new ApiException("Transaction not found or already confirmed.", HttpStatus.NOT_FOUND));
  }

  @CacheEvict(value = "walletBalanceCache", key = "#message.sourceWalletId")
  public void process(TransactionMessage message) {
    log.info("m=process, step=start, transactionCode={}", message.getTransactionCode());
    Transaction transaction = transactionRepository.findByTransactionCode(message.getTransactionCode())
        .orElseThrow(() -> new RuntimeException("Transaction not found"));

    try {
      switch (TransactionType.valueOf(message.getType())) {
        case DEPOSIT:
          processDeposit(message);
          break;
        case WITHDRAW:
          processWithdraw(message);
          break;
        case TRANSFER:
          processTransfer(message);
          break;
        default:
          throw new IllegalArgumentException("Unsupported transaction type");
      }

      transaction.setStatus(TransactionStatus.COMPLETED);
      transactionRepository.save(transaction);
      log.info("m=process, step=completed, transactionCode={}", message.getTransactionCode());
    } catch (Exception e) {
      transaction.setStatus(TransactionStatus.FAILED);
      transaction.setErrorMessage(e.getMessage());
      transactionRepository.save(transaction);
      log.error("Failed to process transaction. TransactionCode={}", message.getTransactionCode(), e);
      throw e;
    }
  }

  @Transactional
  public void processDeposit(TransactionMessage message) {
    log.info("m=processDeposit, step=init, transactionCode={}", message.getTransactionCode());

    walletServiceHelper.updateWalletBalance(
        message.getSourceWalletId(),
        message.getAmount(),
        TransactionType.DEPOSIT,
        message.getTransactionCode().toString()
    );

    log.info("m=processDeposit, step=end, transactionCode={}", message.getTransactionCode());
  }

  @Transactional
  public void processWithdraw(TransactionMessage message) {
    log.info("m=processWithdraw, step=init, transactionCode={}", message.getTransactionCode());

    walletServiceHelper.updateWalletBalance(
        message.getSourceWalletId(),
        message.getAmount().negate(),
        TransactionType.WITHDRAW,
        message.getTransactionCode().toString()
    );

    log.info("m=processWithdraw, step=end, transactionCode={}", message.getTransactionCode());
  }

  @CacheEvict(value = "walletBalanceCache", key = "#message.targetWalletId")
  @Transactional
  public void processTransfer(TransactionMessage message) {
    log.info("m=processTransfer, step=init, transactionCode={}", message.getTransactionCode());

    walletServiceHelper.updateWalletBalance(
        message.getSourceWalletId(),
        message.getAmount().negate(),
        TransactionType.TRANSFER,
        message.getTransactionCode().toString()
    );

    walletServiceHelper.updateWalletBalance(
        message.getTargetWalletId(),
        message.getAmount(),
        TransactionType.TRANSFER,
        message.getTransactionCode().toString()
    );

    log.info("m=processTransfer, step=end, transactionCode={}", message.getTransactionCode());
  }
}

package com.recargapay.walletservice.scheduler;

import com.recargapay.walletservice.entity.Transaction;
import com.recargapay.walletservice.enums.TransactionStatus;
import com.recargapay.walletservice.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransactionTimeoutService {

  private final TransactionRepository transactionRepository;

  private static final int TIMEOUT_MINUTES = 2;

  @Scheduled(fixedDelay = 3600000)
  public void cancelExpiredPendingTransactions() {
    log.info("m=cancelExpiredPendingTransactions, step=init");

    LocalDateTime expirationThreshold = LocalDateTime.now().minus(TIMEOUT_MINUTES, ChronoUnit.MINUTES);

    List<Transaction> expiredTransactions = transactionRepository
        .findByStatusAndCreatedAtBefore(TransactionStatus.PENDING, expirationThreshold);

    for (Transaction transaction : expiredTransactions) {
      transaction.setStatus(TransactionStatus.CANCELLED);
      transaction.setUpdatedAt(LocalDateTime.now());
      log.info("m=cancelExpiredPendingTransactions, step=cancel, transactionCode={}", transaction.getTransactionCode());
    }

    transactionRepository.saveAll(expiredTransactions);

    log.info("m=cancelExpiredPendingTransactions, step=end, totalCancelled={}", expiredTransactions.size());
  }

}

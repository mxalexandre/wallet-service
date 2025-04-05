package com.recargapay.walletservice.repository;

import com.recargapay.walletservice.entity.Transaction;
import com.recargapay.walletservice.enums.TransactionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findByTransactionCode(UUID transactionCode);

  Optional<Transaction> findByTransactionCodeAndStatus(UUID transactionCode, TransactionStatus status);

  List<Transaction> findByStatusAndCreatedAtBefore(TransactionStatus status, LocalDateTime dateTime);
}

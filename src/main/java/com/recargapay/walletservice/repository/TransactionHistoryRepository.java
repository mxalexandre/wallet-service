package com.recargapay.walletservice.repository;

import com.recargapay.walletservice.entity.TransactionHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

  @Query("SELECT w FROM TransactionHistory w " +
      "WHERE w.walletId = :walletId " +
      "AND FORMATDATETIME(w.createdAt,'yyyy-MM-dd') = :date " +
      "ORDER BY w.createdAt DESC")
  Optional<TransactionHistory> findTopByWalletIdAndCreatedAtDate(
      @Param("walletId") Long walletId,
      @Param("date") LocalDate date
  );

  Optional<TransactionHistory> findFirstByWalletIdAndCreatedAtBetweenOrderByCreatedAtDesc(
      Long walletId,
      LocalDateTime startOfDay,
      LocalDateTime endOfDay
  );

}

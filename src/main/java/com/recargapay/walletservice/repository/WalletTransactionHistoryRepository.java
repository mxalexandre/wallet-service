package com.recargapay.walletservice.repository;

import com.recargapay.walletservice.entity.WalletTransactionHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletTransactionHistoryRepository extends JpaRepository<WalletTransactionHistory, Long> {

  @Query("SELECT w FROM WalletTransactionHistory w " +
      "WHERE w.walletId = :walletId " +
      "AND FORMATDATETIME(w.createdAt,'yyyy-MM-dd') = :date " +
      "ORDER BY w.createdAt DESC")
  Optional<WalletTransactionHistory> findTopByWalletIdAndCreatedAtDate(
      @Param("walletId") Long walletId,
      @Param("date") LocalDate date
  );

  Optional<WalletTransactionHistory> findFirstByWalletIdAndCreatedAtBetweenOrderByCreatedAtDesc(
      Long walletId,
      LocalDateTime startOfDay,
      LocalDateTime endOfDay
  );

}

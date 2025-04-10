package com.recargapay.walletservice.entity;

import com.recargapay.walletservice.controller.request.CreateTransactionRequest;
import com.recargapay.walletservice.enums.TransactionStatus;
import com.recargapay.walletservice.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "transaction_code", nullable = false, unique = true, updatable = false)
  private UUID transactionCode;

  @Column(name = "source_wallet_id", nullable = false)
  private Long sourceWalletId;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private TransactionStatus status;

  @Column(name = "target_wallet_id")
  private Long targetWalletId;

  @Column(name = "error_message", length = 500)
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PreUpdate
  void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @PrePersist
  void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public static Transaction from(final CreateTransactionRequest request) {
    return Transaction.builder()
        .transactionCode(UUID.randomUUID())
        .sourceWalletId(request.getSourceWalletId())
        .targetWalletId(request.getTargetWalletId())
        .type(request.getType())
        .amount(request.getAmount())
        .status(TransactionStatus.PENDING)
        .build();
  }
}

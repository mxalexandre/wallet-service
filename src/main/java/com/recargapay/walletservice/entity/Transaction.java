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
@Table(name = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID transactionCode;

  @Column(nullable = false)
  private Long sourceWalletId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionStatus status;

  private Long targetWalletId;

  private String errorMessage;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @PreUpdate
  void onUpdate() { this.updatedAt = LocalDateTime.now(); }

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


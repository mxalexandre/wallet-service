package com.recargapay.walletservice.repository;

import com.recargapay.walletservice.entity.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

  boolean existsByOwnerDocument(String ownerDocument);

  Optional<Wallet> findByOwnerDocument(String ownerDocument);
}

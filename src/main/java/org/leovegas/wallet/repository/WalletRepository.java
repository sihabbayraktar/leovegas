package org.leovegas.wallet.repository;

import org.leovegas.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Wallet> findByUserId(UUID userId);
}

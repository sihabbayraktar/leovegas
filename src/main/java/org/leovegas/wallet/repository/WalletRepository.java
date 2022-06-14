package org.leovegas.wallet.repository;

import org.leovegas.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT u FROM Wallet u WHERE u.userId = ?1")
    Optional<Wallet> selectForUpdateByUserId(UUID userId);

    Optional<Wallet> findByUserId(UUID userId);
}

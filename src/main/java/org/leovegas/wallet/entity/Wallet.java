package org.leovegas.wallet.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ALL")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private UUID userId;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "wallet")
    @ToString.Exclude
    private List<Transaction> transactionList;

    @Version
    private Long version;
}

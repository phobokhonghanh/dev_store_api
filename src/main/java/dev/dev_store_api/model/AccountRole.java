package dev.dev_store_api.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_role", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "role_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_role_account"))
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_role_role"))
    private Role role;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public AccountRole(Account account, Role role) {
        this.account = account;
        this.role = role;
    }
}

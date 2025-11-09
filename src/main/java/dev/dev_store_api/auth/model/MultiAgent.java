package dev.dev_store_api.auth.model;

import dev.dev_store_api.account.model.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "multi_agent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultiAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_multi_agent_account"))
    private Account account;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String agent;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String token;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public MultiAgent(Account account, String agent, String ipAddress, String token, String refreshToken, boolean isActive) {
        this.account = account;
        this.agent = agent;
        this.ipAddress = ipAddress;
        this.token = token;
        this.refreshToken = refreshToken;
        this.isActive = isActive;
    }
}

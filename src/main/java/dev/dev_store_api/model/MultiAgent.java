package dev.dev_store_api.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}

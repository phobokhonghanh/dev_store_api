package dev.dev_store_api.account.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_relation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Account parent;

    // Quan hệ con
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Account child;

    // Loại quan hệ (VD: SUB_ACCOUNT, MANAGER, FRIEND, …)
    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    @Column(nullable = false, name = "status", columnDefinition = "INTEGER DEFAULT 1")
    private Integer status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

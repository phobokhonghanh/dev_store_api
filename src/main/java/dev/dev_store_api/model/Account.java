package dev.dev_store_api.model;

import java.time.LocalDateTime;

import dev.dev_store_api.model.type.EProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 10)
    private String otpCode;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer status;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EProvider authProvider;

    @Column(length = 255)
    private String authProviderId;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, insertable = false)
    private LocalDateTime updatedAt;
}

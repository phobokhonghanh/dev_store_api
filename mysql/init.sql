--  1. Tạo user & cấp quyền
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

--  2. Tạo database & sử dụng
CREATE DATABASE IF NOT EXISTS dev_login CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dev_login;

--  4. Bảng account (tài khoản)
CREATE TABLE IF NOT EXISTS account (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10),
    status INTEGER NOT NULL DEFAULT 1,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    avatar TEXT,
    auth_provider VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    auth_provider_id TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

--  5. Bảng role (quyền hạn)
CREATE TABLE IF NOT EXISTS role (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    status INTEGER DEFAULT 1, -- 1: Active, 0: Inactive
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO role (name, description, status)
VALUES
    ('ADMIN', 'Administrator role', 1),
    ('USER', 'Regular user role', 1)
ON DUPLICATE KEY UPDATE name = name;

--  6. Bảng account_role (phân quyền)
CREATE TABLE IF NOT EXISTS account_role (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_role_account
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_account_role_role
        FOREIGN KEY (role_id)
        REFERENCES role(id)
        ON DELETE CASCADE,
    UNIQUE KEY unique_account_role (account_id, role_id)
) ENGINE=InnoDB;

--  7. Bảng multi_agent (thiết bị)
CREATE TABLE IF NOT EXISTS multi_agent (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT UNSIGNED NOT NULL,
    agent TEXT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    token TEXT NOT NULL,
    refresh_token TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE, -- true: đang hoạt động, false: không hoạt động
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_multi_agent_account
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

--  8. Bảng account_relation (quan hệ)
CREATE TABLE IF NOT EXISTS account_relation (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT UNSIGNED NOT NULL,
    child_id BIGINT UNSIGNED NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    status INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_account_relation_parent
        FOREIGN KEY (parent_id)
        REFERENCES account(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_account_relation_child
        FOREIGN KEY (child_id)
        REFERENCES account(id)
        ON DELETE CASCADE,

    CONSTRAINT unique_relation UNIQUE (parent_id, child_id, relation_type)
) ENGINE=InnoDB;

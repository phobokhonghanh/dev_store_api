GRANT ALL PRIVILEGES ON *.* TO 'admin'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS dev_login;
USE dev_login;

CREATE TABLE IF NOT EXISTS third_party (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    link TEXT NOT NULL,
    status INTEGER DEFAULT 1 -- 1: Active, 0: Inactive
);

INSERT INTO third_party (name, link, status)
VALUES ('SYSTEM', 'DEFAULT', 1)
    ON DUPLICATE KEY UPDATE name = name;


CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10),
    status INTEGER NOT NULL DEFAULT 1,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    avatar TEXT,
    third_party_id BIGINT UNSIGNED REFERENCES third_party(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    status INTEGER DEFAULT 1, -- 1: Active, 0: Inactive
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO role (name, description, status)
VALUES
    ('ADMIN', 'Administrator role', 1),
    ('USER', 'Regular user role', 1)
    ON DUPLICATE KEY UPDATE name = name;

CREATE TABLE IF NOT EXISTS account_role (
    id SERIAL PRIMARY KEY,
    account_id BIGINT UNSIGNED REFERENCES account(id) ON DELETE CASCADE,
    role_id BIGINT UNSIGNED REFERENCES role(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (account_id, role_id)
);

CREATE TABLE IF NOT EXISTS multi_agent (
    id SERIAL PRIMARY KEY,
    account_id BIGINT UNSIGNED REFERENCES account(id) ON DELETE CASCADE,
    agent TEXT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    token TEXT NOT NULL,
    refresh_token TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE, -- Trạng thái đăng nhập (true: đang hoạt động, false: không hoạt động)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



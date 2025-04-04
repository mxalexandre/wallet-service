CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(140) NOT NULL,
    owner_document VARCHAR(14) NOT NULL,
    balance DECIMAL(19,2) NOT NULL
);

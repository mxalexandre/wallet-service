-- Create table for users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create table for wallets
CREATE TABLE IF NOT EXISTS wallet (
    id SERIAL PRIMARY KEY,
    owner_name VARCHAR(255),
    owner_document VARCHAR(255),
    balance NUMERIC(19,2) NOT NULL,
    reserved_amount NUMERIC(19,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create table for transactions
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    transaction_code UUID NOT NULL UNIQUE,
    source_wallet_id BIGINT NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    target_wallet_id BIGINT,
    error_message VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (source_wallet_id) REFERENCES wallet(id),
    FOREIGN KEY (target_wallet_id) REFERENCES wallet(id)
);

-- Create table for transaction history
CREATE TABLE IF NOT EXISTS transaction_history (
    id SERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    amount NUMERIC(19,2),
    type VARCHAR(50),
    transaction_code VARCHAR(255),
    balance_before NUMERIC(19,2),
    balance_after NUMERIC(19,2),
    created_at TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallet(id)
);

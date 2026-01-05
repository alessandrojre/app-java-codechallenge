CREATE TABLE transactions (
                              id UUID PRIMARY KEY, --transactionExternalId
                              account_external_id_debit UUID NOT NULL,
                              account_external_id_credit UUID NOT NULL,
                              transfer_type_id INT NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              value DECIMAL(19,2) NOT NULL,
                              created_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
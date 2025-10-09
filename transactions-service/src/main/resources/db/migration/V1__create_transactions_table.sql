CREATE TABLE transaction (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    from_account_id UUID NOT NULL,
    to_account_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    from_account_version_id UUID NOT NULL,
    to_account_version_id UUID NOT NULL
);

CREATE TABLE account (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    account_number VARCHAR(50) NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    version_id UUID NOT NULL       
);

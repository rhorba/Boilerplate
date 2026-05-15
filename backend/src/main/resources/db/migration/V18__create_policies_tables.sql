CREATE TABLE policies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    effect VARCHAR(20) NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_policies_resource_action ON policies(resource, action);
CREATE INDEX idx_policies_enabled ON policies(enabled);

CREATE TABLE policy_conditions (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    subject VARCHAR(20) NOT NULL,
    attribute_key VARCHAR(100) NOT NULL,
    operator VARCHAR(20) NOT NULL,
    attribute_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_policy_conditions_policy_id ON policy_conditions(policy_id);

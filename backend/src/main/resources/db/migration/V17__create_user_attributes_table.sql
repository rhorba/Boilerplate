CREATE TABLE user_attributes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attribute_key VARCHAR(100) NOT NULL,
    attribute_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_user_attribute UNIQUE (user_id, attribute_key)
);

CREATE INDEX idx_user_attributes_user_id ON user_attributes(user_id);
CREATE INDEX idx_user_attributes_key ON user_attributes(attribute_key);

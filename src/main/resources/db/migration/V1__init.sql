CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE actions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    password VARCHAR(255),
    role_id BIGINT REFERENCES roles(id)
);

CREATE TABLE user_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    owner_id BIGINT REFERENCES users(id)
);

CREATE TABLE pages (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    slug VARCHAR(255) UNIQUE,
    content TEXT,
    icon VARCHAR(255),
    roles VARCHAR(255),
    schema TEXT,
    access_control TEXT
);

CREATE TABLE page_data (
    id BIGSERIAL PRIMARY KEY,
    page_id BIGINT,
    data TEXT
);

CREATE TABLE activity_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255),
    description VARCHAR(255),
    user_email VARCHAR(255),
    timestamp TIMESTAMP
);

CREATE TABLE user_group_members (
    user_id BIGINT NOT NULL REFERENCES users(id),
    group_id BIGINT NOT NULL REFERENCES user_groups(id)
);

CREATE TABLE group_pages (
    group_id BIGINT NOT NULL REFERENCES user_groups(id),
    page_id BIGINT NOT NULL REFERENCES pages(id)
);


CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_type VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP
);


INSERT INTO roles (role_type) VALUES ('ADMIN');
INSERT INTO roles (role_type) VALUES ('USER');


CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email_id VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP
);


CREATE TABLE user_roles (
    user_id BIGSERIAL NOT NULL,
    role_id BIGSERIAL NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY(user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE,
    FOREIGN KEY(role_id) 
        REFERENCES roles(id)
        ON DELETE CASCADE
);

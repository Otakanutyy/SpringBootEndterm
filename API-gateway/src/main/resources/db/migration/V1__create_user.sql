CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    customer_id BIGINT
);

CREATE INDEX idx_email ON users (email);

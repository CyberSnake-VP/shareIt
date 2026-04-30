CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name varchar(255) NOT NULL,
    email varchar(320) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_email UNIQUE (email)
);
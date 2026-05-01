CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name varchar(255) NOT NULL,
    email varchar(320) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED ALWAYS AS IDENTITY,
  name varchar(255) NOT NULL,
  description varchar(1000) NOT NULL,
  available boolean NOT NULL,
  owner_id BIGINT NOT NULL ,
  CONSTRAINT pk_items PRIMARY KEY (id),
  CONSTRAINT fk_items_users FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ✅ Обязательно (частые запросы по владельцу)
CREATE INDEX IF NOT EXISTS idx_items_owner_id ON items (owner_id);

-- ✅ Для поиска по доступности
CREATE INDEX IF NOT EXISTS idx_items_available ON items (available);

-- Для поиска по названию
CREATE INDEX IF NOT EXISTS idx_items_name ON items (name);
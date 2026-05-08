CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name  varchar(255) NOT NULL,
    email varchar(320) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY,
    name        varchar(255)  NOT NULL,
    description varchar(1000) NOT NULL,
    available   boolean       NOT NULL,
    owner_id    BIGINT        NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_users FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITH TIME ZONE NOT NULL,
    item_id    BIGINT                   NOT NULL,
    booker_id  BIGINT                   NOT NULL,
    status     varchar(100)             NOT NULL,
    CONSTRAINT pk_key PRIMARY KEY (id),
    CONSTRAINT fk_bookings_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_users FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    text         TEXT                     NOT NULL,
    item_id      BIGINT                   NOT NULL,
    author_id    BIGINT                   NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    description varchar(1000),
    requestor_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT fk_requests_users

);

CREATE INDEX IF NOT EXISTS idx_comments_item_id ON comments (item_id);
CREATE INDEX IF NOT EXISTS idx_comments_author_id ON comments (author_id);
CREATE INDEX IF NOT EXISTS idx_comments_created_date ON comments (created_date DESC);



CREATE INDEX IF NOT EXISTS idx_bookings_item_id ON bookings (item_id);

CREATE INDEX IF NOT EXISTS idx_bookings_booker_id ON bookings (booker_id);

CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings (status);

-- Частый запрос: бронирования пользователя по статусу
CREATE INDEX IF NOT EXISTS idx_bookings_booker_status ON bookings (booker_id, status);

-- Частый запрос: пересечение дат для предмета
CREATE INDEX IF NOT EXISTS idx_bookings_item_dates ON bookings (item_id, start_date, end_date);

-- Частый запрос: владелец вещи + статус
CREATE INDEX IF NOT EXISTS idx_bookings_owner_status ON bookings (item_id, status);


-- ✅ Обязательно (частые запросы по владельцу)
CREATE INDEX IF NOT EXISTS idx_items_owner_id ON items (owner_id);

-- ✅ Для поиска по доступности
CREATE INDEX IF NOT EXISTS idx_items_available ON items (available);

-- Для поиска по названию
CREATE INDEX IF NOT EXISTS idx_items_name ON items (name);


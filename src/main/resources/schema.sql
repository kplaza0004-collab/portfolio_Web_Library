-- 会員テーブル
CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    last_login_at TIMESTAMP
);

-- 書籍履歴テーブル (idはBIGSERIAL/Bigint型)
CREATE TABLE IF NOT EXISTS book_history (
    id BIGSERIAL PRIMARY KEY,
    google_book_id VARCHAR(191) UNIQUE,
    title VARCHAR(255),
    subtitle VARCHAR(255),
    authors VARCHAR(255),
    publisher VARCHAR(255),
    published_date VARCHAR(255),
    description TEXT,
    page_count INTEGER,
    image_link VARCHAR(1000),
    canonical_volume_link VARCHAR(255)
);

-- レンタルテーブル
CREATE TABLE IF NOT EXISTS rentals (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    book_history_id BIGINT NOT NULL,
    rental_date TIMESTAMP NOT NULL,
    returned_date TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES accounts(id),
    FOREIGN KEY (book_history_id) REFERENCES book_history(id)
);

-- お気に入りテーブル
CREATE TABLE IF NOT EXISTS favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    google_book_id VARCHAR(191) NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES accounts(id),
    FOREIGN KEY (google_book_id) REFERENCES book_history(google_book_id)
);
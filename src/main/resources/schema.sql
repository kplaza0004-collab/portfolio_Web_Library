-- 会員テーブル
CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 書籍履歴テーブル
CREATE TABLE IF NOT EXISTS book_history (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255),
    authors VARCHAR(255),
    publisher VARCHAR(100),
    published_date VARCHAR(50),
    image_link VARCHAR(500)
);

-- レンタルテーブル
CREATE TABLE IF NOT EXISTS rentals (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    book_history_id VARCHAR(100) NOT NULL,
    rental_date TIMESTAMP NOT NULL,
    returned_date TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES accounts(id),
    FOREIGN KEY (book_history_id) REFERENCES book_history(id)
);

-- お気に入りテーブル
CREATE TABLE IF NOT EXISTS favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    book_history_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES accounts(id),
    FOREIGN KEY (book_history_id) REFERENCES book_history(id)
);
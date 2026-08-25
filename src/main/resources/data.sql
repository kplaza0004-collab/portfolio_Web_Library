-- 一般ユーザー
INSERT INTO accounts (id, password, role) 
VALUES ('A1234567', '$2a$10$fRTz5iyrXlkqtP/T.58oLuBm6jYwG44fAXJiGYNWPYSEtKF0JDdWu', 'USER')
ON CONFLICT (id) DO NOTHING;

-- 管理者ユーザー (ROLE_ADMIN または ADMIN の表記揺れに対応)
INSERT INTO accounts (id, password, role) 
VALUES ('Z1234567', '$2a$10$fRTz5iyrXlkqtP/T.58oLuBm6jYwG44fAXJiGYNWPYSEtKF0JDdWu', 'ADMIN')
ON CONFLICT (id) DO NOTHING;
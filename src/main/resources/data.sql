-- Insert roles only if the table is empty
INSERT INTO roles (name)
SELECT 'GENERAL' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'GENERAL');

INSERT INTO roles (name)
SELECT 'PATIENT' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'PATIENT');

INSERT INTO roles (name)
SELECT 'DOCTOR' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'DOCTOR');

INSERT INTO roles (name)
SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

-- Insert users with correct role_id and default is_doctor flag
INSERT INTO users (username, password, role_id, access_code, fear_level, coins, is_doctor, created_at, updated_at)
VALUES
('john_doe', 'password123', (SELECT id FROM roles WHERE name = 'GENERAL'), NULL, 10, 3, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane_doe', 'password456', (SELECT id FROM roles WHERE name = 'PATIENT'), 'FFANM001', 2, 6, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('doctor_smith', 'password789', (SELECT id FROM roles WHERE name = 'DOCTOR'), NULL, 0, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', '$2a$10$hashed_admin_password', (SELECT id FROM roles WHERE name = 'ADMIN'), NULL, 0, 0, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert assessments
INSERT INTO assessments (user_id, score, fear_percentage)
VALUES
(1, 85, 85.0),
(2, 90, 90.5);

-- Insert game progress
INSERT INTO game_progress (user_id, animal_type, current_level, completed)
VALUES
(1, 'CAT', 1, FALSE),
(2, 'DOG', 10, TRUE);

-- Insert rewards
INSERT INTO rewards (name, description, coin_cost)
VALUES
('Ice Cream', 'Strawberry ice cream', 3),
('Zoo Voucher', 'Voucher for zoo entry', 6);

-- Insert reward redemptions
INSERT INTO reward_redemptions (user_id, reward_id, redeemed_at)
VALUES
(1, 1, CURRENT_TIMESTAMP),
(2, 2, CURRENT_TIMESTAMP);

INSERT INTO flight (id, arrival, departure, destination, origin, status)
SELECT
    100000 + n,
    NOW() + (INTERVAL '1 day' * (FLOOR(RANDOM() * 30 + 1)::int)) +
    (INTERVAL '1 second' * (FLOOR(RANDOM() * 3600) * (FLOOR(RANDOM() * 27) - 12)::int)),
    NOW() + (INTERVAL '1 day' * (FLOOR(RANDOM() * 30 + 1)::int)) +
    (INTERVAL '1 second' * (FLOOR(RANDOM() * 3600) * (FLOOR(RANDOM() * 27) - 12)::int)) - INTERVAL '12 hours',
    UPPER(SUBSTR(MD5(RANDOM()::TEXT), 1, 3)),
    UPPER(SUBSTR(MD5(RANDOM()::TEXT), 1, 3)),
    CASE (n % 3)
        WHEN 0 THEN 'IN_TIME'
        WHEN 1 THEN 'DELAYED'
        ELSE 'CANCELLED'
        END
FROM generate_series(0, 99) AS t(n);


INSERT INTO roles (id, code)
VALUES (1, 'USER'),
       (2, 'MODERATOR');


INSERT INTO users (id, password, username, role_id) VALUES
    (999, '$2a$10$auEBxZrH9PeLxO3eKuSZgOlpzQGaUgfBR9BV/cbsVt5mwRP1Yysmi', 'moderator', 2);
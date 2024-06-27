insert into roles (id, code) values
    (1, 'USER'),
    (2, 'MODERATOR');

insert into users (id, password, username, role_id) VALUES
    (999, '$2a$10$auEBxZrH9PeLxO3eKuSZgOlpzQGaUgfBR9BV/cbsVt5mwRP1Yysmi', 'moderator', 2);


INSERT INTO flight (id, arrival, departure, destination, origin, status)
SELECT
    100000 + n,
    DATEADD('SECOND', FLOOR(RAND() * 3600) * (FLOOR(RAND() * 27) - 12), DATEADD('DAY', FLOOR(RAND() * 30) + 1, CURRENT_TIMESTAMP)),
    DATEADD('SECOND', FLOOR(RAND() * 3600) * (FLOOR(RAND() * 27) - 12), DATEADD('DAY', FLOOR(RAND() * 30) + 1, CURRENT_TIMESTAMP) - INTERVAL '12' HOUR),
    SUBSTRING('ABCDEFGHIJKMNOPQRSTUVWXYZ', FLOOR(RAND() * 26 + 1), FLOOR(RAND() * 2 + 3)),
    SUBSTRING('ABCDEFGHIJKMNOPQRSTUVWXYZ', FLOOR(RAND() * 26 + 1), FLOOR(RAND() * 2 + 3)),
    CASE MOD(n, 3)
        WHEN 0 THEN 'IN_TIME'
        WHEN 1 THEN 'DELAYED'
        ELSE 'CANCELLED'
        END
FROM SYSTEM_RANGE(0, 99) AS t(n);

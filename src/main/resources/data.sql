-- Test 계정
INSERT INTO user (id, username, password, email, nickname, role, created_at, updated_at, removed_at) values
(1, 'master', '$2a$10$LfrEytcMjgXAnB/iOmPoqOyIMsrC.NG4rAqMI8nuFWMjdovj6gamO', 'master@naver.com', 'master', 'ADMIN', now(), null, null)
;

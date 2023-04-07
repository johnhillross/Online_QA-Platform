INSERT INTO user (id, username, password, email) VALUES (1, 'Ross', '$2a$10$tlTUfYPyxNGLvQ7Ku5EXAeNXoj2FY8E2rjkjNQQ4ILAzoxpRYqSvq', 'johnhillross@163.com');

INSERT INTO authority (id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO authority (id, role) VALUES (2, 'ROLE_USER');

INSERT INTO user_authority (user_id, authority_id) VALUES (1, 1);
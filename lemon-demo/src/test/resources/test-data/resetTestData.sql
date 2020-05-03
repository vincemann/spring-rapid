SET @@foreign_key_checks = 0;
TRUNCATE TABLE usr;
TRUNCATE TABLE usr_role;


INSERT INTO usr(id, email, password, name, credentials_updated_millis)
VALUES (101, 'admin@example.com', '{bcrypt}$2a$10$nzg1V.oO8HIgtny2HaaJdObxwZIzz9HvbFP8nW0BfBID4COq8AZMe', 'Admin 1', 0);
INSERT INTO usr_role(user_id, role) VALUES (101, 'ROLE_ADMIN');

INSERT INTO usr(id, email, password, name, credentials_updated_millis)
VALUES (102, 'unverifiedadmin@example.com', '{bcrypt}$2a$10$nzg1V.oO8HIgtny2HaaJdObxwZIzz9HvbFP8nW0BfBID4COq8AZMe', 'Unverified Admin', 0);
INSERT INTO usr_role(user_id, role) VALUES (102, 'ROLE_ADMIN');
INSERT INTO usr_role(user_id, role) VALUES (102, 'ROLE_UNVERIFIED');

INSERT INTO usr(id, email, password, name, credentials_updated_millis)
VALUES (103, 'blockedadmin@example.com', '{bcrypt}$2a$10$nzg1V.oO8HIgtny2HaaJdObxwZIzz9HvbFP8nW0BfBID4COq8AZMe', 'Blocked Admin', 0);
INSERT INTO usr_role(user_id, role) VALUES (103, 'ROLE_ADMIN');
INSERT INTO usr_role(user_id, role) VALUES (103, 'ROLE_BLOCKED');

INSERT INTO usr(id, email, password, name, credentials_updated_millis)
VALUES (104, 'user@example.com', '{bcrypt}$2a$10$YYqgZ6j8uOncPaUouDM8QOyOzgU875GFcLDSAW7u9kQxXexeCXYpi', 'User', 0);

INSERT INTO usr(id, email, password, name, credentials_updated_millis)
VALUES (105, 'unverifieduser@example.com', '{bcrypt}$2a$10$YYqgZ6j8uOncPaUouDM8QOyOzgU875GFcLDSAW7u9kQxXexeCXYpi', 'Unverified User', 0);
INSERT INTO usr_role(user_id, role) VALUES (105, 'ROLE_UNVERIFIED');

INSERT INTO usr(id, email, password, name, credentials_updated_millis)
VALUES (106, 'blockeduser@example.com', '{bcrypt}$2a$10$YYqgZ6j8uOncPaUouDM8QOyOzgU875GFcLDSAW7u9kQxXexeCXYpi', 'Blocked User', 0);
INSERT INTO usr_role(user_id, role) VALUES (106, 'ROLE_BLOCKED');
SET @@foreign_key_checks = 1;
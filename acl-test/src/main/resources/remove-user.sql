-- Disable foreign key constraints
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM PUBLIC.usr;
-- Re-enable foreign key constraints
SET FOREIGN_KEY_CHECKS = 1;
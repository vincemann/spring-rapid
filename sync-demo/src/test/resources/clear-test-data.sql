-- Disable foreign key constraints
SET FOREIGN_KEY_CHECKS = 0;

-- Clean up existing data
DELETE FROM PUBLIC.owners;
DELETE FROM PUBLIC.pets;
DELETE FROM PUBLIC.clinic_cards;
DELETE FROM PUBLIC.pet_types;
DELETE FROM PUBLIC.toys;

-- Re-enable foreign key constraints
SET FOREIGN_KEY_CHECKS = 1;
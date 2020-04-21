SET @@foreign_key_checks = 0;
TRUNCATE TABLE acl_class;
TRUNCATE TABLE acl_entry;
TRUNCATE TABLE acl_object_identity;
TRUNCATE TABLE acl_sid;
SET @@foreign_key_checks = 1;
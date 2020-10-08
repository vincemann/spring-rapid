SET @@foreign_key_checks = 0;
delete from acl_class;
delete from acl_entry;
delete from acl_object_identity;
delete from acl_sid;
SET @@foreign_key_checks = 1;
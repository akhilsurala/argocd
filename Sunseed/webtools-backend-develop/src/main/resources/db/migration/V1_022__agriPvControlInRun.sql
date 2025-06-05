Alter table user_run
ADD COLUMN pv_control BOOLEAN DEFAULT FALSE,
ADD COLUMN agri_control BOOLEAN DEFAULT FALSE;

Alter table user_run
DROP COLUMN variant_exist;
ALTER TABLE user_run 
ADD COLUMN clone_id bigint,
ADD COLUMN is_master boolean DEFAULT true;
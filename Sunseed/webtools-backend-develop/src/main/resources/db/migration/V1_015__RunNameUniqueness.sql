ALTER TABLE pre_processor_toggle
ADD CONSTRAINT run_name_unique_constraint_on_pre_processor_toggle UNIQUE (project_id, run_name);

ALTER TABLE user_run
ADD CONSTRAINT run_name_unique_constraint_on_user_run UNIQUE (project_id,run_name);
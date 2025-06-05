ALTER TABLE IF EXISTS simulation_tasks
ADD COLUMN task_execution_time_on_server BIGINT,
ADD COLUMN server_name VARCHAR(255),
ADD COLUMN has_highest_radiation BOOLEAN DEFAULT FALSE;

ALTER TABLE IF EXISTS agri_block_simulation_details
ADD COLUMN bed_name VARCHAR(255);
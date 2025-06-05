CREATE TABLE IF NOT EXISTS simulation_blocks (
    id BIGSERIAL PRIMARY KEY,
    block_index INT,
    block_start_date DATE,
    block_end_date DATE,
    block_simulation_date DATE,
    cycle_start_date DATE,
    cycle_duration_in_days INT,
    cycle_end_date DATE,
    cycle_name VARCHAR(255),
    running_days_in_block_for_pv INT,
    simulation_id BIGINT,
    CONSTRAINT fk_simulation_id FOREIGN KEY (simulation_id) REFERENCES simulations (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS agri_block_simulation_details (
    id BIGSERIAL PRIMARY KEY,
    running_days_in_block INT,
    crop_age INT,
    bed_index INT,
    crop_name VARCHAR(255),
    crop_start_date DATE,
    crop_end_date DATE,
    duration INT,
    min_stage INT,
    max_stage INT,
    simulation_block_id BIGINT,
    CONSTRAINT fk_simulation_block_id FOREIGN KEY (simulation_block_id) REFERENCES simulation_blocks (id) ON DELETE CASCADE
);

ALTER TABLE simulation_tasks
ADD COLUMN IF NOT EXISTS simulation_block_id BIGINT;

ALTER TABLE simulation_tasks
ADD CONSTRAINT fk_simulation_block_id FOREIGN KEY (simulation_block_id)
REFERENCES simulation_blocks (id) ON DELETE CASCADE;

ALTER TABLE simulation_tasks
DROP COLUMN week_sequence;

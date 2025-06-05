-- Drop the old unique constraint
ALTER TABLE simulation_ground_area
DROP CONSTRAINT IF EXISTS simulation_ground_area_simulation_id_key;

-- Drop the foreign key constraint on simulation_id
ALTER TABLE simulation_ground_area
DROP CONSTRAINT IF EXISTS simulation_ground_area_simulation_id_fkey;

-- Remove the simulation_id column
ALTER TABLE simulation_ground_area
DROP COLUMN IF EXISTS simulation_id;

-- Add the simulation_block_id column
ALTER TABLE simulation_ground_area
ADD COLUMN simulation_block_id BIGINT;

-- Add a new foreign key constraint for simulation_block_id
ALTER TABLE simulation_ground_area
ADD CONSTRAINT simulation_ground_area_simulation_block_id_fkey
FOREIGN KEY (simulation_block_id)
REFERENCES simulation_blocks (id);

-- Add a unique constraint for simulation_block_id and date
ALTER TABLE simulation_ground_area
ADD CONSTRAINT simulation_ground_area_simulation_block_id_key
UNIQUE (simulation_block_id);

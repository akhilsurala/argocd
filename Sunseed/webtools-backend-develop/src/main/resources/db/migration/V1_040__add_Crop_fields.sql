-- Add new columns with default values that won't break existing data
ALTER TABLE crop ADD COLUMN has_plant_actual_date BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE crop ADD COLUMN plant_actual_start_date VARCHAR NULL;
ALTER TABLE crop ADD COLUMN plant_max_age INT NULL;
ALTER TABLE crop ADD COLUMN max_plants_per_bed INT NULL;

-- Set default values for existing records before enforcing NOT NULL constraints
UPDATE crop SET plant_max_age = 0 WHERE plant_max_age IS NULL;
UPDATE crop SET max_plants_per_bed = 3 WHERE max_plants_per_bed IS NULL;



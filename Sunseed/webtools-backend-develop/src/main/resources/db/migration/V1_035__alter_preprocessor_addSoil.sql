ALTER TABLE agri_general_parameter
DROP CONSTRAINT IF EXISTS FK_CROPPARAMETERS_ON_SOILID;

ALTER TABLE agri_general_parameter
DROP COLUMN IF EXISTS soil_id;

-- Step 3: Add soilTypeId column to pre_processor_toggles
ALTER TABLE pre_processor_toggle
ADD COLUMN soil_id BIGINT;

ALTER TABLE pre_processor_toggle
ADD CONSTRAINT fk_pre_processor_toggle_soil
FOREIGN KEY (soil_id) REFERENCES soil_type(id)
ON DELETE SET NULL ON UPDATE CASCADE;
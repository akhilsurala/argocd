ALTER TABLE soil_type
ADD COLUMN optical_property_id BIGINT;

-- Add a foreign key constraint linking soil to optical_property
ALTER TABLE soil_type
ADD CONSTRAINT fk_soil_optical_property
    FOREIGN KEY (optical_property_id)
    REFERENCES optical_property(id)
    ON DELETE SET NULL;
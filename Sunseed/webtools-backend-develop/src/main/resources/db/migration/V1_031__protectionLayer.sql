ALTER TABLE protection_layer
ADD COLUMN polysheets VARCHAR(255) ,
ADD COLUMN link_to_texture VARCHAR(255) ,
ADD COLUMN diffusion_fraction DOUBLE PRECISION,
ADD COLUMN transmission_percentage DOUBLE PRECISION,
ADD COLUMN void_percentage DOUBLE PRECISION ,
ADD COLUMN f1 DOUBLE PRECISION,
ADD COLUMN f2 DOUBLE PRECISION,
ADD COLUMN f3 DOUBLE PRECISION,
ADD COLUMN f4 DOUBLE PRECISION;

-- Add the optical_property_id foreign key if not already present
ALTER TABLE protection_layer
ADD COLUMN optical_property_id BIGINT,
ADD CONSTRAINT fk_protection_layer_optical_property
    FOREIGN KEY (optical_property_id)
    REFERENCES optical_property(id)
    ON DELETE SET NULL;

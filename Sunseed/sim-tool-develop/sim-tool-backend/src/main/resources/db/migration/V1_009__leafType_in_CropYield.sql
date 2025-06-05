ALTER TABLE IF EXISTS crop_yields 
ADD leaf_type VARCHAR(255),
ADD saturation numeric,
ADD radiation numeric,
ADD latent_flux numeric,
ADD leaves_area numeric,
ADD penetration numeric,
ADD crop_count integer;
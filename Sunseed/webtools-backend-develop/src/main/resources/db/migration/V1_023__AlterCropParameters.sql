
ALTER TABLE crop_parameters
ADD COLUMN master_cycle_id bigint[];

ALTER TABLE crop_bed_section
ADD COLUMN stretch DOUBLE PRECISION;

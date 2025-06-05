ALTER TABLE bed_parameter
    ALTER COLUMN bed_azimuth DROP NOT NULL,
    ALTER COLUMN start_point_offset DROP NOT NULL;


ALTER TABLE bed ALTER COLUMN bed_name SET NOT NULL;

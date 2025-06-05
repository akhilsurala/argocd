CREATE TABLE IF NOT EXISTS protection_layer (
    protection_layer_id bigserial NOT NULL,
    protection_layer_name VARCHAR(255),
    is_active   BOOLEAN DEFAULT TRUE,
    hide        BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

ALTER TABLE protection_layer ADD CONSTRAINT protection_layer_pk PRIMARY KEY (protection_layer_id);


CREATE TABLE IF NOT EXISTS bed_parameter (
    id  bigserial NOT NULL,
    bed_width DOUBLE PRECISION NOT NULL,
    bed_height DOUBLE PRECISION NOT NULL,
    bed_angle INTEGER NOT NULL,
    bed_azimuth INTEGER NOT NULL,
    bedcc DOUBLE PRECISION NOT NULL,
    start_point_offset INTEGER NOT NULL,
    agri_general_parameter    BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

ALTER TABLE bed_parameter ADD CONSTRAINT bed_parameter_pk PRIMARY KEY (id);




CREATE TABLE IF NOT EXISTS irrigation (
    id bigserial NOT NULL,
    irrigation_type VARCHAR(255),
    is_active    BOOLEAN DEFAULT TRUE,
    hide         BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

ALTER TABLE irrigation ADD CONSTRAINT irrigation_pk PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS soil_type (
    id bigserial NOT NULL,
    soil_name VARCHAR(255),
    soil_picture_path VARCHAR(255),
    is_active    BOOLEAN DEFAULT TRUE,
    hide         BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

ALTER TABLE soil_type ADD CONSTRAINT soil_type_pk PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS agri_pv_protection_height (
    id  bigserial NOT NULL,
    agri_pv_id BIGINT,
    protection_layer_id BIGINT,
    protection_height INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
   
);
ALTER TABLE agri_pv_protection_height ADD CONSTRAINT agri_pv_protection_height_pk PRIMARY KEY (id);


ALTER TABLE agri_pv_protection_height ADD CONSTRAINT protection_layer_fk FOREIGN KEY (protection_layer_id) REFERENCES protection_layer(protection_layer_id);

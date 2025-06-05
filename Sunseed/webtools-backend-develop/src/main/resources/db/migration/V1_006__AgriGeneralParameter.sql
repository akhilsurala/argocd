CREATE TABLE IF NOT EXISTS agri_general_parameter (
    id                   bigserial NOT NULL,
    irrigation_id        BIGINT NOT NULL,
    soil_id              BIGINT NOT NULL,
    temp_control          VARCHAR(255),
    trail                 INTEGER,
    min_temp              INTEGER ,
    max_temp              INTEGER,
    is_mulching         BOOLEAN NOT NULL,
    project_id           BIGINT NOT NULL,
    status               VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ  
    
);
ALTER TABLE agri_general_parameter ADD CONSTRAINT agri_general_pk PRIMARY KEY (id);


ALTER TABLE agri_general_parameter ADD CONSTRAINT irrigation_fk FOREIGN KEY (irrigation_id) REFERENCES irrigation(id);

ALTER TABLE agri_general_parameter ADD CONSTRAINT soil_type_fk FOREIGN KEY (soil_id) REFERENCES soil_type(id);
ALTER TABLE agri_general_parameter ADD CONSTRAINT project_type_fk FOREIGN KEY (project_id) REFERENCES projects(project_id);

ALTER TABLE bed_parameter ADD CONSTRAINT agri_general_parameter_fk FOREIGN KEY (agri_general_parameter) REFERENCES agri_general_parameter(id);

ALTER TABLE agri_pv_protection_height ADD CONSTRAINT agri_pv_parameter_fk FOREIGN KEY (agri_pv_id) REFERENCES agri_general_parameter(id);


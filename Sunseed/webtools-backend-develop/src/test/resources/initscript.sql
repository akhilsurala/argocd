DO $$
BEGIN
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'agri_pv_protection_height') THEN
--        DROP TABLE agri_pv_protection_height CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'pv_parameter_module_config') THEN
--        DROP TABLE pv_parameter_module_config CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'pv_parameter') THEN
--        DROP TABLE pv_parameter CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'mode_of_pv_operation') THEN
--        DROP TABLE mode_of_pv_operation CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'pv_module_configuration') THEN
--        DROP TABLE pv_module_configuration CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'pv_module') THEN
--        DROP TABLE pv_module CASCADE;
--    END IF;
--
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_run') THEN
        DROP TABLE user_run CASCADE;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'projects') THEN
        DROP TABLE projects CASCADE;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_profile') THEN
        DROP TABLE user_profile CASCADE;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_otp') THEN
        DROP TABLE user_otp CASCADE;
    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'protection_layer') THEN
--        DROP TABLE protection_layer CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'bed_parameter') THEN
--        DROP TABLE bed_parameter CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'agri_general_parameter') THEN
--        DROP TABLE agri_general_parameter CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'irrigation') THEN
--        DROP TABLE irrigation CASCADE;
--    END IF;
--
--    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'soil_type') THEN
--        DROP TABLE soil_type CASCADE;
--    END IF;
END $$;

-- Table Creation Statements
CREATE TABLE user_otp
(
    otp_id          bigserial NOT NULL,
    otp             INTEGER,
    user_profile_id BIGINT,
    otp_status      VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ,
    CONSTRAINT pk_user_otp PRIMARY KEY (otp_id)
);

CREATE TABLE user_profile
(
    user_profile_id      bigserial NOT NULL,
    first_name           VARCHAR(255),
    last_name            VARCHAR(255),
    email_id             VARCHAR(255),
    phone_number         VARCHAR(255),
    profile_picture_path VARCHAR(255),
    user_id              BIGINT,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    CONSTRAINT pk_user_profile PRIMARY KEY (user_profile_id)
);

CREATE TABLE projects
(
    project_id           bigserial NOT NULL,
    project_name         VARCHAR(255),
    user_profile_id      BIGINT,
    latitude             VARCHAR(255),
    longitude            VARCHAR(255),
    project_status       VARCHAR(255),
    area                 double precision,
    polygon_coordinates  VARCHAR(5000),
    comments             VARCHAR(255),
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ, 
    CONSTRAINT pk_projects PRIMARY KEY (project_id)
);

CREATE TABLE user_run
(
    run_id                bigserial NOT NULL,
    project_id            BIGINT,
    run_name              VARCHAR(255),
    created_at            TIMESTAMPTZ NOT NULL,
    updated_at            TIMESTAMPTZ,
    CONSTRAINT pk_user_run PRIMARY KEY (run_id)
);

--CREATE TABLE pv_module
--(
--    id          bigserial NOT NULL,
--    module_type VARCHAR(255),
--    created_at  TIMESTAMPTZ NOT NULL,
--    updated_at  TIMESTAMPTZ,
--    CONSTRAINT pk_pvmodule PRIMARY KEY (id)
--);
--
--CREATE TABLE mode_of_pv_operation
--(
--    id                bigserial NOT NULL,
--    mode_of_operation VARCHAR(255),
--    created_at        TIMESTAMPTZ NOT NULL,
--    updated_at        TIMESTAMPTZ,
--    CONSTRAINT pk_modeofpvoperation PRIMARY KEY (id)
--);
--
--CREATE TABLE pv_module_configuration
--(
--    id            bigserial NOT NULL,
--    module_config VARCHAR(255),
--    created_at    TIMESTAMPTZ NOT NULL,
--    updated_at    TIMESTAMPTZ,
--    CONSTRAINT pk_pvmoduleconfiguration PRIMARY KEY (id)
--);
--
--CREATE TABLE pv_parameter
--(
--    id                    bigserial NOT NULL,
--    run_name              VARCHAR(255),
--    tilt_if_ft            INTEGER,
--    max_angle_of_tracking INTEGER,
--    module_mask_pattern   VARCHAR(255),
--    gap_between_modules   INTEGER,
--    height                INTEGER,
--    pitch_of_rows         INTEGER,
--    azimuth               INTEGER,
--    length_of_one_row     INTEGER,
--    module_type           BIGINT,
--    mode_of_operation_id  BIGINT,
--    project_id            BIGINT,
--    status                VARCHAR(255),
--    created_at            TIMESTAMPTZ NOT NULL,
--    updated_at            TIMESTAMPTZ,
--    CONSTRAINT pk_pvparameter PRIMARY KEY (id)
--);
--
--CREATE TABLE protection_layer (
--    protection_layer_id bigserial NOT NULL,
--    protection_layer_name VARCHAR(255),
--    created_at TIMESTAMPTZ NOT NULL,
--    updated_at TIMESTAMPTZ,
--    CONSTRAINT protection_layer_pk PRIMARY KEY (protection_layer_id)
--);
--
--CREATE TABLE bed_parameter (
--    id  bigserial NOT NULL,
--    bed_width INTEGER NOT NULL,
--    bed_height INTEGER NOT NULL,
--    bed_angle INTEGER NOT NULL,
--    bed_azimuth INTEGER NOT NULL,
--    bedcc INTEGER NOT NULL,
--    agri_general_parameter BIGINT NOT NULL,
--    created_at TIMESTAMPTZ NOT NULL,
--    updated_at TIMESTAMPTZ,
--    CONSTRAINT bed_parameter_pk PRIMARY KEY (id)
--);

-- Foreign Key Constraints
ALTER TABLE user_profile ADD CONSTRAINT FK_PROJECTS_ON_USER_PROFILE FOREIGN KEY (user_profile_id) REFERENCES user_profile (user_profile_id);
ALTER TABLE user_run ADD CONSTRAINT FK_USER_RUN_ON_PROJECTID FOREIGN KEY (project_id) REFERENCES projects (project_id);
--ALTER TABLE pv_parameter ADD CONSTRAINT FK_PVPARAMETER_ON_MODEOFOPERATIONID FOREIGN KEY (mode_of_operation_id) REFERENCES mode_of_pv_operation (id);
--ALTER TABLE pv_parameter ADD CONSTRAINT FK_PVPARAMETER_ON_MODULETYPE FOREIGN KEY (module_type) REFERENCES pv_module (id);
--ALTER TABLE pv_parameter ADD CONSTRAINT FK_PVPARAMETER_ON_PROJECTID FOREIGN KEY (project_id) REFERENCES projects (project_id);
--ALTER TABLE bed_parameter ADD CONSTRAINT agri_general_parameter_fk FOREIGN KEY (agri_general_parameter) REFERENCES agri_general_parameter(id);



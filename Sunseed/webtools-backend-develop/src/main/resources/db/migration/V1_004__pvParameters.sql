CREATE TABLE pv_parameter
(
    id                    bigserial NOT NULL,
    tilt_if_ft            DOUBLE PRECISION,
    max_angle_of_tracking DOUBLE PRECISION,
    module_mask_pattern   VARCHAR(255),
    gap_between_modules   DOUBLE PRECISION,
    height                DOUBLE PRECISION,
    x_coordinate          DOUBLE PRECISION,
    y_coordinate          DOUBLE PRECISION,
    module_type           BIGINT,
    mode_of_operation_id  BIGINT,
    project_id            BIGINT,
    status                VARCHAR(255),
    created_at            TIMESTAMPTZ NOT NULL,
    updated_at            TIMESTAMPTZ,
    CONSTRAINT pk_pvparameter PRIMARY KEY (id)
);



ALTER TABLE pv_parameter
    ADD CONSTRAINT FK_PVPARAMETER_ON_MODEOFOPERATIONID FOREIGN KEY (mode_of_operation_id) REFERENCES mode_of_pv_operation (id);

ALTER TABLE pv_parameter
    ADD CONSTRAINT FK_PVPARAMETER_ON_MODULETYPE FOREIGN KEY (module_type) REFERENCES pv_module (id);

ALTER TABLE pv_parameter
    ADD CONSTRAINT FK_PVPARAMETER_ON_PROJECTID FOREIGN KEY (project_id) REFERENCES projects (project_id);

ALTER TABLE pv_parameter_module_config
    ADD CONSTRAINT fk_pvparmodcon_on_pv_module_configuration FOREIGN KEY (module_config_id) REFERENCES pv_module_configuration (id);

ALTER TABLE pv_parameter_module_config
    ADD CONSTRAINT fk_pvparmodcon_on_pv_parameter FOREIGN KEY (pv_parameter_id) REFERENCES pv_parameter (id);

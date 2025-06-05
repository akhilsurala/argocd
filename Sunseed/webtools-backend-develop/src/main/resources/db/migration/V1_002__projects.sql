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

ALTER TABLE projects
    ADD CONSTRAINT FK_PROJECTS_ON_USER_PROFILE FOREIGN KEY (user_profile_id) REFERENCES user_profile (user_profile_id);

CREATE TABLE user_run
(
    run_id                bigserial NOT NULL,
    run_name              VARCHAR(255),
    is_simulated          BOOLEAN default false,
    is_active             boolean default true,
    can_simulate          boolean default true,
    created_at            TIMESTAMPTZ NOT NULL,
    updated_at            TIMESTAMPTZ,
    project_id BIGINT,
    pre_processor_toggle_id  bigint NOT NULL,
    pv_parameter_id       bigint,
    crop_parameters_id    bigint,
    agri_general_parameters_id bigint,
    
    CONSTRAINT pk_user_run PRIMARY KEY (run_id)
);

ALTER TABLE user_run
    ADD CONSTRAINT FK_USER_RUN_ON_PROJECTID FOREIGN KEY (project_id) REFERENCES projects (project_id);

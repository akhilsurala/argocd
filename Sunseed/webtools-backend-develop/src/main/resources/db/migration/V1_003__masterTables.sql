CREATE TABLE pv_module
(
    id          bigserial NOT NULL,
    module_type VARCHAR(255),
    length      Double PRECISION,
    width       DOUBLE PRECISION,
    is_active   BOOLEAN DEFAULT TRUE,
    hide        BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ,
    CONSTRAINT pk_pvmodule PRIMARY KEY (id)
);

CREATE TABLE mode_of_pv_operation
(
    id                bigserial NOT NULL,
    mode_of_operation VARCHAR(255),
    is_active         BOOLEAN DEFAULT TRUE,
    hide              BOOLEAN DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL,
    updated_at        TIMESTAMPTZ,
    CONSTRAINT pk_modeofpvoperation PRIMARY KEY (id)
);

CREATE TABLE pv_module_configuration
(
    id            bigserial NOT NULL,
    module_config VARCHAR(255),
    ordering INT NOT NULL,
    number_of_modules INT NOT NULL,
    type_of_module VARCHAR(255) NOT NULL,
    is_active   BOOLEAN DEFAULT TRUE,
    hide        BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL,
    updated_at    TIMESTAMPTZ,
    CONSTRAINT pk_pvmoduleconfiguration PRIMARY KEY (id)
);

CREATE TABLE pv_parameter_module_config
(
    module_config_id bigserial NOT NULL,
    pv_parameter_id  bigserial NOT NULL
);

CREATE TABLE crop (
    id bigserial NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_active    BOOLEAN DEFAULT TRUE,
    hide         BOOLEAN DEFAULT TRUE,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    CONSTRAINT pk_crop_id PRIMARY KEY (id)
);


CREATE TABLE crop_parameters (
    id bigserial NOT NULL,
    project_id BIGINT,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    status               VARCHAR(255),
    FOREIGN KEY (project_id) REFERENCES projects(project_id),
    CONSTRAINT pk_crop_parameters_id PRIMARY KEY (id)
);


CREATE TABLE cycles (
    id bigserial not null,
    name VARCHAR(255) NOT NULL,
    start_date DATE,
    crop_parameters_id BIGINT,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    inter_bed_pattern	JSONB,
    FOREIGN KEY (crop_parameters_id) REFERENCES crop_parameters(id),
    CONSTRAINT pk_cycles_id PRIMARY KEY (id)
);

CREATE TABLE bed (
    id bigserial not null,
    cycle_id BIGINT,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    FOREIGN KEY (cycle_id) REFERENCES cycles(id),
    CONSTRAINT pk_bed_id PRIMARY KEY (id)
);

CREATE TABLE crop_bed_section (
    id bigserial not null,
    crop_id BIGINT,
    o1 BIGINT,
    s1 BIGINT,
    o2 BIGINT,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,	
    FOREIGN KEY (crop_id) REFERENCES crop(id),
    bed_id BIGINT,
    FOREIGN KEY (bed_id) REFERENCES bed(id),
    CONSTRAINT pk_crop_bed_section_id PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS currency (
currency_id   bigserial NOT NULL,
        currency               VARCHAR(255),
   created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ

);

ALTER TABLE currency ADD CONSTRAINT currency_pk PRIMARY KEY (currency_id);


CREATE TABLE IF NOT EXISTS economic_parameters (
    economic_id                   bigserial NOT NULL,
    currency_id                 BIGINT NOT NULL,
    economic_parameter           BOOLEAN NOT NULL ,
    min_reference_yield          DOUBLE PRECISION,
    max_reference_yield          DOUBLE PRECISION,
    min_input_cost_of_crop      DOUBLE PRECISION,
    max_input_cost_of_crop       DOUBLE PRECISION,
    min_selling_cost_of_crop     DOUBLE PRECISION,
    max_selling_cost_of_crop     DOUBLE PRECISION,
    hourly_selling_rates       INTEGER[],
    project_id           BIGINT NOT NULL,
        status               VARCHAR(255),
   created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ 
    
);

ALTER TABLE economic_parameters ADD CONSTRAINT economic_pk PRIMARY KEY (economic_id);


ALTER TABLE economic_parameters ADD CONSTRAINT  project_type_fk FOREIGN KEY (project_id) REFERENCES projects(project_id);
ALTER TABLE economic_parameters ADD CONSTRAINT  currency_type_fk FOREIGN KEY (currency_id) REFERENCES currency(currency_id);

ALTER TABLE economic_parameters
DROP COLUMN  min_reference_yield,
DROP COLUMN  max_reference_yield,
DROP COLUMN  min_input_cost_of_crop,
DROP COLUMN  max_input_cost_of_crop,
DROP COLUMN  min_selling_cost_of_crop,
DROP COLUMN  max_selling_cost_of_crop;



CREATE TABLE IF NOT EXISTS economic_multicrop (
    id                           bigserial PRIMARY KEY ,
    crop_id                      BIGINT NOT NULL REFERENCES crop(id) ,
    min_reference_yield          DOUBLE PRECISION,
    max_reference_yield          DOUBLE PRECISION,
    min_input_cost_of_crop       DOUBLE PRECISION,
    max_input_cost_of_crop       DOUBLE PRECISION,
    min_selling_cost_of_crop     DOUBLE PRECISION,
    max_selling_cost_of_crop     DOUBLE PRECISION,
    cultivation_area             DOUBLE PRECISION,
    economic_parameters_id       BIGINT REFERENCES economic_parameters(economic_id),
    created_at                   TIMESTAMPTZ NOT NULL,
    updated_at                   TIMESTAMPTZ

);
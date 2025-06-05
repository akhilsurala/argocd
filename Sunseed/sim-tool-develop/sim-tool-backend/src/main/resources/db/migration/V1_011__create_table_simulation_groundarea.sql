CREATE TABLE IF NOT EXISTS simulation_ground_area (
    id 				bigserial PRIMARY KEY,
    simulation_id 	BIGINT,
    unit_x_length 	numeric,
    unit_y_length 	numeric,
    x_repetition 	integer,
    y_repetition 	integer,
    x_length 		numeric,
    y_length 		numeric,
    created_at 		timestamp without time zone,
    updated_at 		timestamp without time zone,
    
    FOREIGN KEY (simulation_id) REFERENCES simulations (id),
    UNIQUE(simulation_id)
);
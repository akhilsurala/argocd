ALTER TABLE pv_module
    ADD COLUMN IF NOT EXISTS num_cell_x INTEGER,
    ADD COLUMN IF NOT EXISTS num_cell_y INTEGER,
    ADD COLUMN IF NOT EXISTS longer_side INTEGER,
    ADD COLUMN IF NOT EXISTS shorter_side INTEGER,
    ADD COLUMN IF NOT EXISTS thickness INTEGER,
    ADD COLUMN IF NOT EXISTS void_ratio FLOAT,
    ADD COLUMN IF NOT EXISTS x_cell FLOAT,
    ADD COLUMN IF NOT EXISTS y_cell FLOAT,
    ADD COLUMN IF NOT EXISTS x_cell_gap FLOAT,
    ADD COLUMN IF NOT EXISTS y_cell_gap FLOAT,
    ADD COLUMN IF NOT EXISTS v_map FLOAT,
    ADD COLUMN IF NOT EXISTS i_map FLOAT,
    ADD COLUMN IF NOT EXISTS idc0 FLOAT,
    ADD COLUMN IF NOT EXISTS pdc0 FLOAT,
    ADD COLUMN IF NOT EXISTS n_effective FLOAT,
    ADD COLUMN IF NOT EXISTS v_oc FLOAT,
    ADD COLUMN IF NOT EXISTS i_sc FLOAT,
    ADD COLUMN IF NOT EXISTS alpha_sc FLOAT,
    ADD COLUMN IF NOT EXISTS beta_voc FLOAT,
    ADD COLUMN IF NOT EXISTS gamma_pdc FLOAT,
    ADD COLUMN IF NOT EXISTS tem_ref FLOAT,
    ADD COLUMN IF NOT EXISTS rad_sun FLOAT,
    ADD COLUMN front_optical_property_id BIGINT,
	ADD COLUMN back_optical_property_id BIGINT,
    ADD COLUMN IF NOT EXISTS f1 DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS f2 DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS f3 DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS f4 DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS f5 DOUBLE PRECISION,
    ADD COLUMN manufacturer_name VARCHAR(255) ,
	ADD COLUMN module_name VARCHAR(255) ,
	ADD COLUMN shortcode VARCHAR(50) ,
	ADD COLUMN module_tech VARCHAR(255) ,
	ADD COLUMN link_to_data_sheet VARCHAR(255);

-- Add optical_property_id column and foreign key constraint to link PvModule and OpticalProperty
ALTER TABLE pv_module
ADD CONSTRAINT fk_pv_module_front_optical_property
FOREIGN KEY (front_optical_property_id)
REFERENCES optical_property(id)
ON DELETE SET NULL;

ALTER TABLE pv_module
ADD CONSTRAINT fk_pv_module_back_optical_property
FOREIGN KEY (back_optical_property_id)
REFERENCES optical_property(id)
ON DELETE SET NULL;

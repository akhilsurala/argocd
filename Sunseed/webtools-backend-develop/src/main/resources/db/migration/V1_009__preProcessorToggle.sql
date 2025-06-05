create table pre_processor_toggle(
	id bigserial NOT NULL,
	toggle varchar(255),
	pre_processor_status varchar(255) default 'draft',
	run_name varchar(255),
	length_of_one_row DOUBLE PRECISION,
	pitch_of_rows DOUBLE PRECISION,
	azimuth DOUBLE PRECISION,
	created_at TIMESTAMPTZ NOT NULL,
	updated_at TIMESTAMPTZ,
	project_id bigint,
	CONSTRAINT pk_pre_processor_toggle primary key (id)
);

ALTER TABLE pre_processor_toggle
    ADD CONSTRAINT FK_PREPROCESSORTOGGLE_ON_PROJECTID FOREIGN KEY (project_id) REFERENCES projects (project_id);
    
ALTER TABLE user_run
	ADD CONSTRAINT FK_USERRUN_ON_PREPROCESSORTOGGLEID FOREIGN KEY (pre_processor_toggle_id) REFERENCES pre_processor_toggle(id);
	
ALTER TABLE user_run
	ADD CONSTRAINT FK_USERRUN_ON_PVPARAMETERID FOREIGN KEY (pv_parameter_id) REFERENCES pv_parameter(id);
	
ALTER TABLE user_run
	ADD CONSTRAINT FK_USERRUN_ON_CROPPARAMETERSID FOREIGN KEY (crop_parameters_id) REFERENCES crop_parameters(id);
	
ALTER TABLE user_run
	ADD CONSTRAINT FK_USERRUN_ON_AGRIGENERALPARAMETERSID FOREIGN KEY (agri_general_parameters_id) REFERENCES agri_general_parameter(id);
	
	

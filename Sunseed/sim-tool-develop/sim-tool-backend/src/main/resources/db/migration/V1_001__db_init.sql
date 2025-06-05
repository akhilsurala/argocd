CREATE TABLE IF NOT EXISTS simulations (
    id                  bigserial               PRIMARY KEY,
    user_profile_id     bigint,
    project_id          bigint,
    run_id              bigint,
    is_completed        boolean,
    task_count          bigint,
    with_tracking       boolean,
    simulation_type	 varchar(20),
    run_payload         jsonb,
    start_date		 timestamp without time zone,
    end_date 		 timestamp without time zone,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone
);

CREATE TABLE IF NOT EXISTS simulation_tasks (
    id                  bigserial               PRIMARY KEY,
    simulation_id       bigint,
    enqueued_at         timestamp without time zone,
    date                timestamp without time zone,
    pv_status           varchar(20),
    agri_status	 varchar(20),
    weather_condition	 jsonb,
    completed_at        timestamp without time zone,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone,
    FOREIGN KEY (simulation_id) REFERENCES simulations (id),
    UNIQUE(simulation_id, date)
);

CREATE TABLE IF NOT EXISTS pv_yields (
    id                  bigserial               PRIMARY KEY,
    simulation_task_id  bigint,
    pv_yield            numeric,
    front_gain          numeric,
    rear_gain           numeric,
    albedo              numeric,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone,
    FOREIGN KEY (simulation_task_id) REFERENCES simulation_tasks (id)
);

CREATE TABLE IF NOT EXISTS tracking_tilt_angles (
    id                  bigserial               PRIMARY KEY,
    simulation_task_id  bigint,
    tilt_angle          numeric,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone,
    FOREIGN KEY (simulation_task_id) REFERENCES simulation_tasks (id)
);

CREATE TABLE IF NOT EXISTS scenes (
    id                  bigserial               PRIMARY KEY,
    simulation_task_id  bigint,
    type                varchar(20),
    url                 text,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone,
    FOREIGN KEY (simulation_task_id) REFERENCES simulation_tasks (id)
);

CREATE TABLE IF NOT EXISTS crop_yields (
    id                  bigserial               PRIMARY KEY,
    simulation_task_id  bigint,
    carbon_assimilation numeric,
    temperature         numeric,
    created_at          timestamp without time zone,
    updated_at          timestamp without time zone,
    FOREIGN KEY (simulation_task_id) REFERENCES simulation_tasks (id)
);

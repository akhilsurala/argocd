CREATE TABLE simulated_runs (
    id BIGSERIAL PRIMARY KEY,
    simulated_id BIGINT,
    run_id BIGINT,
    project_id BIGINT,
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ,

    FOREIGN KEY (run_id) REFERENCES user_run(run_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);
ALTER TABLE simulated_runs ADD CONSTRAINT uq_simulated_runs_simulated_id_run_id UNIQUE (simulated_id, run_id);
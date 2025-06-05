ALTER TABLE e2e_machine_nodes
ALTER COLUMN current_load TYPE NUMERIC(10,2)
USING ROUND(current_load::NUMERIC, 2);
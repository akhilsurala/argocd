-- Add currentLoad to e2e_machine_nodes
ALTER TABLE e2e_machine_nodes
ADD COLUMN current_load double precision DEFAULT 0.0;

-- Add cpuRequired and ramRequired to simulation_tasks
ALTER TABLE simulation_tasks
ADD COLUMN cpu_required integer DEFAULT 1;

ALTER TABLE simulation_tasks
ADD COLUMN ram_required double precision DEFAULT 1.0;
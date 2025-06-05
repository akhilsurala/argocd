-- Create table for e2e_machine_specifications
CREATE TABLE e2e_machine_specifications (
    id BIGSERIAL PRIMARY KEY,
    machine_name VARCHAR(255),
    plan VARCHAR(255) UNIQUE,
    image VARCHAR(255),
    location VARCHAR(255),
    region VARCHAR(255),

    os_name VARCHAR(255),
    os_version VARCHAR(255),
    os_image VARCHAR(255),
    os_category VARCHAR(255),

    ram DOUBLE PRECISION,
    cpu INTEGER,
    disk_space BIGINT,
    price_per_month DOUBLE PRECISION,
    price_per_hour DOUBLE PRECISION,

    card_name VARCHAR(255),
    template_id BIGINT,
    memory_unit VARCHAR(255),
    memory BIGINT,
    card_type VARCHAR(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);

-- Create table for e2e_machine_nodes
CREATE TABLE e2e_machine_nodes (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT,
    node_name VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255),
    vm_id BIGINT,
    node_created_at_e2e VARCHAR(255),

    public_ip_address VARCHAR(255),
    private_ip_address VARCHAR(255),
    backup BOOLEAN,
    disk VARCHAR(255),
    status VARCHAR(255),
    vcpus INTEGER,
    memory VARCHAR(255),
    plan VARCHAR(255),
    region VARCHAR(255),
    is_locked BOOLEAN,
    zabbix_host_id VARCHAR(255),
    zabbix_host_id_v2 VARCHAR(255),
    gpu VARCHAR(255),
    price VARCHAR(255),
    label VARCHAR(255),
    is_active BOOLEAN,
    scaler_id INTEGER,

    os_name VARCHAR(255),
    os_version VARCHAR(255),
    os_image VARCHAR(255),
    os_category VARCHAR(255),

    is_monitored BOOLEAN,

    backup_status VARCHAR(255),
    backup_detail VARCHAR(255),
    backup_node_id BIGINT,
    backup_is_encryption_enabled BOOLEAN,

    location VARCHAR(255),

    monitor_status VARCHAR(255),
    monitor_reason VARCHAR(255),

    show_bitninja BOOLEAN,
    bitninja_cost DOUBLE PRECISION,

    is_bitninja_license_active BOOLEAN,

    is_license_attached BOOLEAN,
    license_deletion_message VARCHAR(255),
    mssql_license_attached BOOLEAN,

    is_committed BOOLEAN,
    audit_log_message TEXT,
    monitoring_tab_enabled BOOLEAN,
    cdp_tab_enabled BOOLEAN,
    alert_tab_enabled BOOLEAN,
    bitninja_discount_percentage DOUBLE PRECISION,
    is_image_deleted BOOLEAN,
    vpc_enabled BOOLEAN,
    is_snapshot_allowed BOOLEAN,
    is_fortigate_vm BOOLEAN,
    rescue_mode_status VARCHAR(255),
    is_upgradable BOOLEAN,
    abuse_flag BOOLEAN,
    currency VARCHAR(255),
    vm_type VARCHAR(255),
    is_accidental_protection BOOLEAN,
    project_name VARCHAR(255),
    resource_type VARCHAR(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    label_id VARCHAR(255),
    load INTEGER,
    capacity INTEGER,

    e2e_machine_specification_id BIGINT NOT NULL,
    CONSTRAINT fk_specification FOREIGN KEY (e2e_machine_specification_id)
        REFERENCES e2e_machine_specifications (id) ON DELETE CASCADE
);

-- Create table for additional_ip (ElementCollection)
CREATE TABLE e2e_machine_node_additional_ip (
    e2e_machinenode_id BIGINT NOT NULL,
    additional_ip VARCHAR(255),
    CONSTRAINT fk_additional_ip_node FOREIGN KEY (e2e_machinenode_id)
        REFERENCES e2e_machine_nodes (id) ON DELETE CASCADE
);

-- Create table for ssh_keys (ElementCollection)
CREATE TABLE e2e_machine_node_ssh_keys (
    e2e_machinenode_id BIGINT NOT NULL,
    label VARCHAR(255),
    ssh_key VARCHAR(2048),
    CONSTRAINT fk_ssh_keys_node FOREIGN KEY (e2e_machinenode_id)
        REFERENCES e2e_machine_nodes (id) ON DELETE CASCADE
);

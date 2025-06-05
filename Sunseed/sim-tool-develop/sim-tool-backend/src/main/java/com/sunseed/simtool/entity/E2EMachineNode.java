package com.sunseed.simtool.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import com.sunseed.simtool.embeddables.BackupStatus;
import com.sunseed.simtool.embeddables.BitNinjaDetails;
import com.sunseed.simtool.embeddables.LicenseInfo;
import com.sunseed.simtool.embeddables.MonitorStatus;
import com.sunseed.simtool.embeddables.OSInfo;
import com.sunseed.simtool.embeddables.SSHKey;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "e2e_machine_nodes")
public class E2EMachineNode extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private long nodeId;
	private String nodeName;
	private String username;
	private String password;
	private long vm_id;

	@Column(name = "node_created_at_e2e")
	private String nodeCreatedAtE2E;

	private String public_ip_address;
	private String private_ip_address;
	private boolean backup;
	private String disk;
	private String status;
	private int vcpus;
	private String memory;
	private String plan;
	private String region;
	private boolean is_locked;
	private String zabbix_host_id;
	private String zabbix_host_id_v2;
	private String gpu;
	private String price;

	@ElementCollection
	@CollectionTable(name = "e2e_machine_node_additional_ip", joinColumns = @JoinColumn(name = "e2e_machinenode_id"))
	@Column(name = "additional_ip")
	private List<String> additional_ip;

	private String label;
	private boolean is_active;
	private Integer scaler_id;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "os_name", column = @Column(name = "os_name")),
			@AttributeOverride(name = "os_version", column = @Column(name = "os_version")),
			@AttributeOverride(name = "os_image", column = @Column(name = "os_image")),
			@AttributeOverride(name = "os_category", column = @Column(name = "os_category")) })
	private OSInfo os;

	private boolean is_monitored;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "backupStatus", column = @Column(name = "backup_status")),
			@AttributeOverride(name = "backupDetail", column = @Column(name = "backup_detail")),
			@AttributeOverride(name = "backupNodeId", column = @Column(name = "backup_node_id")),
			@AttributeOverride(name = "backup_is_encryption_enabled", column = @Column(name = "backup_is_encryption_enabled")) })
	private BackupStatus backup_status;

	private String location;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "monitorStatus", column = @Column(name = "monitor_status")),
			@AttributeOverride(name = "monitorReason", column = @Column(name = "monitor_reason")) })
	private MonitorStatus monitor_status;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "show_bitninja", column = @Column(name = "show_bitninja")),
			@AttributeOverride(name = "bitninja_cost", column = @Column(name = "bitninja_cost")) })
	private BitNinjaDetails enable_bitninja_details;

	private boolean is_bitninja_license_active;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "is_license_attached", column = @Column(name = "is_license_attached")),
			@AttributeOverride(name = "license_deletion_message", column = @Column(name = "license_deletion_message")),
			@AttributeOverride(name = "mssql_license_attached", column = @Column(name = "mssql_license_attached")) })
	private LicenseInfo any_license_attached;

	private boolean is_committed;
	private String audit_log_message;
	private boolean monitoring_tab_enabled;
	private boolean cdp_tab_enabled;
	private boolean alert_tab_enabled;
	private double bitninja_discount_percentage;
	private boolean is_image_deleted;
	private boolean vpc_enabled;
	private boolean is_snapshot_allowed;
	private boolean is_fortigate_vm;
	private String rescue_mode_status;
	private boolean is_upgradable;
	private boolean abuse_flag;
	private String currency;
	private String vm_type;
	private boolean is_accidental_protection;
	private String project_name;
	private String resource_type;
	private String label_id;
	private int load;
	
	@Column(precision = 10,scale = 2)
	private BigDecimal currentLoad = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	private int capacity;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "e2e_machine_specification_id", nullable = false)
	private E2EMachineSpecifications specifications;

	public void setVcpus(String vcpus) {
		try {
			this.vcpus = Integer.parseInt(Optional.ofNullable(vcpus).orElse("0"));
			this.capacity = calculateCapacityFromVcpus(this.vcpus);
		} catch (NumberFormatException e) {
			this.vcpus = 0; // Default to 0 if parsing fails
			this.capacity = 0; // Default capacity to 0 if vcpus parsing fails
		}
	}

	private int calculateCapacityFromVcpus(int vcpus) {
		return (int) Math.floor(vcpus * 0.9); // Capacity will be 90% of vcpus
	}

	@ElementCollection
	@CollectionTable(name = "e2e_machine_node_ssh_keys", joinColumns = @JoinColumn(name = "e2e_machinenode_id"))
	@Column(name = "ssh_key")
	private List<SSHKey> ssh_keys;
}
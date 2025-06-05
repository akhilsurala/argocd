package com.sunseed.simtool.entity;

import java.util.ArrayList;
import java.util.List;

import com.sunseed.simtool.embeddables.GpuCardDetails;
import com.sunseed.simtool.embeddables.MachineSpecifications;
import com.sunseed.simtool.embeddables.OSInfo;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "e2e_machine_specifications")
public class E2EMachineSpecifications extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String machineName; // name
	private String plan;
	private String image;
	private String location;
	private String region;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "os_name", column = @Column(name = "os_name")),
			@AttributeOverride(name = "os_version", column = @Column(name = "os_version")),
			@AttributeOverride(name = "os_image", column = @Column(name = "os_image")),
			@AttributeOverride(name = "os_category", column = @Column(name = "os_category")) })
	private OSInfo os;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "RAM", column = @Column(name = "ram")),
			@AttributeOverride(name = "CPU", column = @Column(name = "cpu")),
			@AttributeOverride(name = "disk_space", column = @Column(name = "disk_space")),
			@AttributeOverride(name = "price_per_month", column = @Column(name = "price_per_month")),
			@AttributeOverride(name = "price_per_hour", column = @Column(name = "price_per_hour")) })
	private MachineSpecifications machineSpecs;

	@Embedded
	@AttributeOverrides({
	    @AttributeOverride(name = "CARD_NAME", column = @Column(name = "card_name")),
	    @AttributeOverride(name = "TEMPLATE_ID", column = @Column(name = "template_id")),
	    @AttributeOverride(name = "MEMORY_UNIT", column = @Column(name = "memory_unit")),
	    @AttributeOverride(name = "MEMORY", column = @Column(name = "memory")),
	    @AttributeOverride(name = "CARD_TYPE", column = @Column(name = "card_type"))
	})
	private GpuCardDetails gpuCardDetails;

	@OneToMany(mappedBy = "specifications", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
	private List<E2EMachineNode> nodes = new ArrayList<>();

}

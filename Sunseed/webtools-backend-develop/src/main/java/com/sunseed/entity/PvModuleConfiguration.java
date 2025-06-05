package com.sunseed.entity;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunseed.enums.PVModuleConfigType;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PvModuleConfiguration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String moduleConfig; // landscape, portrait

	private int ordering;

	private int numberOfModules;

	@Enumerated(EnumType.STRING)
	private PVModuleConfigType typeOfModule;

	@Builder.Default
	private Boolean isActive = true;

	@Builder.Default
	private Boolean hide = true;

	@ManyToMany(mappedBy = "moduleConfig",fetch= FetchType.LAZY)
	@JsonIgnore
	@JsonBackReference
	private List<PvParameter> pvParameter;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;
}
package com.sunseed.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sunseed.enums.PreProcessorStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CropParameters {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "projectId")
	@JsonBackReference
	private Projects project;
	
	@OneToMany(mappedBy = "cropParameters", cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
	@Builder.Default
	List<Cycles> cycles = new ArrayList<>();
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private PreProcessorStatus status=PreProcessorStatus.DRAFT;

	private Long[] masterCycleId;

	@OneToOne(mappedBy = "cropParameters")
	@JsonBackReference
	private Runs run;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
	public void setCycles(List<Cycles> cycles) {
		if(this.cycles!=null)
		this.cycles.clear();
		if(cycles!=null) {
			this.cycles.addAll(cycles);
		}
	}
}
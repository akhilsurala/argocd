package com.sunseed.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Bed {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bedName;
	
//	private Long cropBedId1;
//	
//	private Long cropBedId2;
	
	@OneToMany(mappedBy = "bed", cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
	@Builder.Default
	private List<CropBedSection> cropBed = new ArrayList<>();	// cropBeds of given Bed
	
	@ManyToOne
	@JoinColumn(name = "cycleId")
	@JsonBackReference
	private Cycles cycle;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;

	public void setCropBed(List<CropBedSection> cropBed) {

		if(this.cropBed!=null)
			this.cropBed.clear();
		if (cropBed != null) {
			this.cropBed.addAll(cropBed);
		}
	}
	
}
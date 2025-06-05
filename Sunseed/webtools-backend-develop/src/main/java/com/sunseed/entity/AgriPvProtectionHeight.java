package com.sunseed.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class
AgriPvProtectionHeight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "agri_pv_id")
	@JsonBackReference
	private AgriGeneralParameter agriGeneralParameter;
	
	
	// ** -> Here change protectionLayer to protectionLayerId
	// ** -> Also check mapping in ProtectionLayer !
	@ManyToOne
	@JoinColumn(name = "protection_layer_id")
	@JsonBackReference
	private ProtectionLayer protectionLayer;
	
	
	
	private Double protectionHeight;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
}

package com.sunseed.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sunseed.enums.CommonStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_otp")
@Builder
public class UserOtp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long otpId;
	private Integer otp;

	private Long userProfileId;

	@Enumerated(EnumType.STRING)
	private CommonStatus otpStatus;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
}

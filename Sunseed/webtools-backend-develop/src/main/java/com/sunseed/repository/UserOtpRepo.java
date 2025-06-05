package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.entity.UserOtp;
import com.sunseed.enums.CommonStatus;

public interface UserOtpRepo extends JpaRepository<UserOtp, Long> {
	List<UserOtp> findByUserProfileId(Long userProfileId);

	Optional<UserOtp> findByOtpStatus(CommonStatus otpStatus);
}

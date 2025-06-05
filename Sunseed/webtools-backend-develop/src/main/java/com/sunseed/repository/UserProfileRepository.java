package com.sunseed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	Optional<UserProfile> findByEmailId(String emailId);

	Optional<UserProfile> findByUserProfileId(Long userProfileId);

	Optional<UserProfile> findByUserId(Long userId);
	
}

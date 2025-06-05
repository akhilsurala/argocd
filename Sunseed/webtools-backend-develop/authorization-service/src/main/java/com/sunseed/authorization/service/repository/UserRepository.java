package com.sunseed.authorization.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunseed.authorization.service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

	Optional<User> findByEmailId(String emailId);
	
	@Query("SELECT u FROM User u WHERE LOWER(u.emailId) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY u.id asc")
	List<User> getAllUsersWithSearchTitle(@Param("searchTitle") String searchTitle);
}

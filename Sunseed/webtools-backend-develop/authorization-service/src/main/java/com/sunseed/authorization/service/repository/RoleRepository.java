package com.sunseed.authorization.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunseed.authorization.service.entity.Role;
import com.sunseed.authorization.service.enums.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	Optional<Role> findByRoleType(RoleType roleType);
}

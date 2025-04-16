package com.nhnacademy.role.repository;

import com.nhnacademy.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findRoleByRoleId(String roleId);

    Boolean existsRoleByRoleId(String roleId);

    Optional<Role> getRoleByRoleId(String roleId);
}

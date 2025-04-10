package com.nhnacademy.exam.javamememberapi.role.repository;

import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> getRoleByRoleId(String roleId);

    Boolean findRoleByRoleId(String roleId);
}

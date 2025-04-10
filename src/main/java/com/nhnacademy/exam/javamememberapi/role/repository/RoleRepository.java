package com.nhnacademy.exam.javamememberapi.role.repository;

import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}

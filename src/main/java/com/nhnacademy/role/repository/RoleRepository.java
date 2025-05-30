package com.nhnacademy.role.repository;

import com.nhnacademy.role.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * RoleRepository는 Role 엔티티에 대한 데이터 액세스 작업을 위한 리포지토리 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 기능을 제공받습니다.
 *
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}

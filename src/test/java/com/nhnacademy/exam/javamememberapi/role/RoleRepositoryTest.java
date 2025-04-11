package com.nhnacademy.exam.javamememberapi.role;

import com.nhnacademy.exam.javamememberapi.role.common.AlreadyExistRoleException;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import com.nhnacademy.exam.javamememberapi.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;


    @BeforeEach
    void setUp(){
        // 테스트 메서드가 실행될 때마다 권한을 등록합니다.
        Role role = new Role(
                "ROLE_ADMIN",
                "관리자",
                "웹 페이지의 관리자 입니다."
        );
        roleRepository.save(role);
    }

    @Test
    @DisplayName("권한 저장")
    void saveRole(){

        Optional<Role> optionalRole = roleRepository.findRoleByRoleId("ROLE_ADMIN");
        Assertions.assertTrue(optionalRole.isPresent());
        Assertions.assertAll(
                () -> Assertions.assertEquals("ROLE_ADMIN", optionalRole.get().getRoleId()),
                () -> Assertions.assertEquals("관리자", optionalRole.get().getRoleName()),
                () -> Assertions.assertEquals("웹 페이지의 관리자 입니다.", optionalRole.get().getRoleDescription())
        );

    }

    @Test
    @DisplayName("권한 가져오기")
    void findRoleByRoleId(){
        Optional<Role> optionalRole = roleRepository.findRoleByRoleId("ROLE_ADMIN");
        Assertions.assertTrue(optionalRole.isPresent());
        Assertions.assertAll(
                () -> Assertions.assertEquals("ROLE_ADMIN", optionalRole.get().getRoleId()),
                () -> Assertions.assertEquals("관리자", optionalRole.get().getRoleName()),
                () -> Assertions.assertEquals("웹 페이지의 관리자 입니다.", optionalRole.get().getRoleDescription())
        );
    }

    @Test
    @DisplayName("권한 존재여부 확인")
    void existsRoleByRoleId(){
        Boolean isExist = roleRepository.existsRoleByRoleId("ROLE_ADMIN");
        Assertions.assertTrue(isExist);
    }

    @Test
    @DisplayName("권한 수정")
    void updateRole(){
        Optional<Role> optionalRole = roleRepository.findRoleByRoleId("ROLE_ADMIN");
        Assertions.assertTrue(optionalRole.isPresent());
        Role role = roleRepository.findRoleByRoleId("ROLE_ADMIN").get();
        role.updateRole("관리자자","관리자자입니다.");
        Assertions.assertAll(
                () -> Assertions.assertEquals("ROLE_ADMIN", role.getRoleId()),
                () -> Assertions.assertEquals("관리자자", role.getRoleName()),
                () -> Assertions.assertEquals("관리자자입니다.", role.getRoleDescription())
        );
    }

    @Test
    @DisplayName("권한 삭제")
    void deleteRole(){
        Optional<Role> optionalRole = roleRepository.findRoleByRoleId("ROLE_ADMIN");
        Assertions.assertTrue(optionalRole.isPresent());
        Role deleteTarget = optionalRole.get();
        roleRepository.delete(deleteTarget);
        Assertions.assertTrue(roleRepository.findRoleByRoleId("ROLE_ADMIN").isEmpty());

    }
}

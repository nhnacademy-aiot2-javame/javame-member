package com.nhnacademy.role.repository;

import com.nhnacademy.common.config.QueryDslConfig;
import com.nhnacademy.role.domain.Role;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@Import({QueryDslConfig.class})
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Role roleUser;
    private Role roleAdmin;
    private Role roleOwner;

    @BeforeEach
    void setUp() {
        // 1. 역할 3개 생성 (USER, ADMIN, OWNER)
        roleUser = new Role("ROLE_USER", "USER", "기본 유저");
        roleAdmin = new Role("ROLE_ADMIN", "ADMIN", "관리자");
        roleOwner = new Role("ROLE_OWNER", "OWNER", "소유주");

        // 2. 역할을 DB에 저장
        testEntityManager.persist(roleUser);
        testEntityManager.persist(roleAdmin);
        testEntityManager.persist(roleOwner);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("역할 저장 및 ID로 조회 테스트")
    void saveAndFindById_WhenRoleExists_ShouldReturnRole() {
        // given: setUp에서 이미 역할들이 저장됨

        // when: 저장된 역할 ID로 조회
        Optional<Role> foundUserRole = roleRepository.findById("ROLE_USER");
        Optional<Role> foundAdminRole = roleRepository.findById("ROLE_ADMIN");
        Optional<Role> foundOwnerRole = roleRepository.findById("ROLE_OWNER");

        // then: 조회가 성공하고, 내용이 일치하는지 확인
        assertThat(foundUserRole).isPresent();
        assertThat(foundUserRole.get().getRoleName()).isEqualTo("USER");
        assertThat(foundUserRole.get().getRoleId()).isEqualTo("ROLE_USER"); // ID 검증 추가

        assertThat(foundAdminRole).isPresent();
        assertThat(foundAdminRole.get().getRoleName()).isEqualTo("ADMIN");
        assertThat(foundAdminRole.get().getRoleId()).isEqualTo("ROLE_ADMIN");

        assertThat(foundOwnerRole).isPresent();
        assertThat(foundOwnerRole.get().getRoleName()).isEqualTo("OWNER");
        assertThat(foundOwnerRole.get().getRoleId()).isEqualTo("ROLE_OWNER");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 Optional.empty 반환")
    void findById_WhenRoleNotExists_ShouldReturnEmptyOptional() {
        // given: 존재하지 않는 역할 ID
        String nonExistentRoleId = "ROLE_GUEST";

        // when: 존재하지 않는 ID로 조회
        Optional<Role> notFoundRole = roleRepository.findById(nonExistentRoleId);

        // then: Optional이 비어있는지 확인
        assertThat(notFoundRole).isNotPresent(); // 또는 .isEmpty()
    }

    @Test
    @DisplayName("역할 전체 조회 테스트")
    void findAllRoles_ShouldReturnAllRoles() {
        // given: setUp에서 3개의 역할이 저장됨

        // when: 전체 역할 조회
        List<Role> roles = roleRepository.findAll();

        // then: 조회된 목록 크기가 3이고, 예상한 역할 이름들을 포함하는지 확인
        assertThat(roles).hasSize(3);
        assertThat(roles).extracting(Role::getRoleName) // 역할 이름만 추출
                .containsExactlyInAnyOrder("USER", "ADMIN", "OWNER"); // 순서 상관없이 포함 여부
    }

    @Test
    @DisplayName("역할 삭제 테스트")
    void deleteRole_ShouldRemoveRole() {
        // given: setUp에서 저장된 roleUser

        // when: 역할 삭제 및 DB 반영
        roleRepository.delete(roleUser);
        roleRepository.flush();

        // then: 삭제된 ID로 조회 시 결과가 없는지 확인
        Optional<Role> deletedRole = roleRepository.findById("ROLE_USER");
        assertThat(deletedRole).isNotPresent();
    }

    @Test
    @DisplayName("역할 업데이트 테스트 (이름 변경)")
    void updateRole_ShouldModifyRoleName() {
        // given: setUp에서 저장된 roleAdmin 조회
        // findById 대신 getReferenceById 사용 가능 (Proxy 객체 반환, 성능상 약간 유리)
        Role roleToUpdate = roleRepository.findById("ROLE_ADMIN").orElseThrow();
        String newRoleName = "SUPER_ADMIN";
        String newRoleDescription = "SUPER_ADMIN DESCRIPTION";

        // when: 역할 이름 변경 및 저장 (JPA 변경 감지로 인해 save는 필수는 아님)
        roleToUpdate.updateRoleDetails(newRoleName, newRoleDescription);
        roleRepository.flush(); // 변경 감지된 내용을 DB에 반영

        // then: 다시 조회하여 이름이 변경되었는지 확인
        Role updatedRole = testEntityManager.find(Role.class, "ROLE_ADMIN");

        assertThat(updatedRole.getRoleName()).isEqualTo(newRoleName);
        assertThat(updatedRole.getRoleDescription()).isEqualTo(newRoleDescription);
    }

    @Test
    @DisplayName("중복된 ID로 역할 저장 시 예외 발생")
    void save_DuplicateRoleId_ShouldThrowException() {
        // given: 이미 존재하는 ID("ROLE_USER")로 새로운 Role 객체 생성
        Role duplicateRole = new Role("ROLE_USER", "USER", "중복 테스트");

        assertThatThrownBy(() -> {
            testEntityManager.persist(duplicateRole);
            testEntityManager.flush(); // DB 제약조건 위반 확인 위해 flush
        }).isInstanceOf(PersistenceException.class);
    }
}
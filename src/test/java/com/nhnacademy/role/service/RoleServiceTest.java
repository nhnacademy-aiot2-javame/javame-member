package com.nhnacademy.role.service; // 실제 패키지 경로에 맞게 수정

import com.nhnacademy.role.common.AlreadyExistRoleException;
import com.nhnacademy.role.common.NotExistRoleException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.request.RoleRegisterRequest;
import com.nhnacademy.role.dto.request.RoleUpdateRequest;
import com.nhnacademy.role.dto.response.RoleResponse;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.role.service.impl.RoleServiceImpl; // 실제 구현 클래스 import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections; // 빈 리스트를 위해 추가
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import static org.junit.jupiter.api.Assertions.assertEquals; // AssertJ 사용 권장
// import static org.junit.jupiter.api.Assertions.assertThrows; // AssertJ 사용 권장
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock // RoleRepository Mock 객체
    private RoleRepository roleRepository;

    @InjectMocks // 테스트 대상 서비스 (RoleRepository Mock 주입)
    private RoleServiceImpl roleService; // 실제 구현 클래스명으로

    private Role roleUser;
    private Role roleAdmin;
    private Role roleOwner; // Owner 역할 추가
    private RoleRegisterRequest roleRegisterRequestUser;
    private RoleUpdateRequest roleUpdateRequestAdmin;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 기본 Role 객체 생성 (DB 저장 아님, 단순 Java 객체)
        roleUser = new Role("ROLE_USER", "USER", "일반 사용자 권한");
        roleAdmin = new Role("ROLE_ADMIN", "ADMIN", "관리자 권한");
        roleOwner = new Role("ROLE_OWNER", "OWNER", "소유자 권한"); // Owner 객체 생성

        // 2. 테스트용 DTO 객체 생성
        roleRegisterRequestUser = new RoleRegisterRequest(
                "ROLE_USER", // roleId
                "USER",      // roleName
                "일반 사용자 권한" // roleDescription
        );

        roleUpdateRequestAdmin = new RoleUpdateRequest(
                "SUPER_ADMIN",          // newRoleName
                "최고 관리자 권한 상세" // newRoleDescription
        );
    }

    @Test
    @DisplayName("역할 등록 성공")
    void registerRole_Success() {
        when(roleRepository.existsById(roleRegisterRequestUser.getRoleId()))
                .thenReturn(false);

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        when(roleRepository.save(roleCaptor.capture())).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });
        RoleResponse createdRoleResponse = roleService.registerRole(roleRegisterRequestUser);

        assertThat(createdRoleResponse).isNotNull();
        assertThat(createdRoleResponse.getRoleId()).isEqualTo(roleRegisterRequestUser.getRoleId());
        assertThat(createdRoleResponse.getRoleName()).isEqualTo(roleRegisterRequestUser.getRoleName());
        assertThat(createdRoleResponse.getRoleDescription()).isEqualTo(roleRegisterRequestUser.getRoleDescription());

        Role capturedRole = roleCaptor.getValue();

        assertThat(capturedRole).isNotNull();
        assertThat(capturedRole.getRoleId()).isEqualTo(roleRegisterRequestUser.getRoleId());
        assertThat(capturedRole.getRoleName()).isEqualTo(roleRegisterRequestUser.getRoleName());
        assertThat(capturedRole.getRoleDescription()).isEqualTo(roleRegisterRequestUser.getRoleDescription());

        verify(roleRepository, times(1)).existsById(roleRegisterRequestUser.getRoleId());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("역할 등록 실패 - 이미 존재하는 역할 ID")
    void registerRole_Fail_RoleIdAlreadyExists() { // 메서드명 구체화
        when(roleRepository.existsById(roleRegisterRequestUser.getRoleId()))
                .thenReturn(true);

        assertThatThrownBy(() -> roleService.registerRole(roleRegisterRequestUser))
                .isInstanceOf(AlreadyExistRoleException.class)
                .hasMessageContaining("해당 권한 ID는 이미 존재합니다: " + roleRegisterRequestUser.getRoleId());

        verify(roleRepository, times(1)).existsById(roleRegisterRequestUser.getRoleId());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("역할 ID로 역할 조회 성공")
    void getRoleById_Success() {
        String existingRoleId = roleUser.getRoleId();
        when(roleRepository.findById(existingRoleId)).thenReturn(Optional.of(roleUser));

        RoleResponse foundRoleResponse = roleService.getRoleById(existingRoleId);

        assertThat(foundRoleResponse).isNotNull();
        assertThat(foundRoleResponse.getRoleId()).isEqualTo(roleUser.getRoleId());
        assertThat(foundRoleResponse.getRoleName()).isEqualTo(roleUser.getRoleName());
        assertThat(foundRoleResponse.getRoleDescription()).isEqualTo(roleUser.getRoleDescription());
        verify(roleRepository, times(1)).findById(existingRoleId);
    }

    @Test
    @DisplayName("역할 ID로 역할 조회 실패 - 존재하지 않는 역할 ID")
    void getRoleById_Fail_RoleIdNotFound() { // 메서드명 구체화
        String nonExistingRoleId = "ROLE_NON_EXISTING";
        when(roleRepository.findById(nonExistingRoleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleById(nonExistingRoleId))
                .isInstanceOf(NotExistRoleException.class)
                .hasMessageContaining("해당 권한이 존재하지 않습니다: ID " + nonExistingRoleId);

        verify(roleRepository, times(1)).findById(nonExistingRoleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("모든 역할 조회 성공 - 역할 목록 반환")
    void getAllRoles_Success_ReturnListOfRoles() { // 메서드명 구체화
        // given - Mock 설정: findAll 호출 시 roleUser와 roleAdmin을 포함하는 리스트 반환
        when(roleRepository.findAll()).thenReturn(List.of(roleUser, roleAdmin));

        // when - 역할 목록 조회
        List<RoleResponse> roleResponses = roleService.getAllRoles();

        // then - 결과 검증
        assertThat(roleResponses).isNotNull();
        assertThat(roleResponses).hasSize(2); // 크기 검증

        // 각 RoleResponse의 내용 검증 (순서가 보장된다면 index로, 아니면 contains 등으로)
        assertThat(roleResponses).extracting(RoleResponse::getRoleId)
                .containsExactlyInAnyOrder(roleUser.getRoleId(), roleAdmin.getRoleId());
        // 필요하다면 이름, 설명도 검증

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("모든 역할 조회 성공 - 역할 없음")
    void getAllRoles_Success_ReturnEmptyListWhenNoRoles() { // 메서드명 구체화
        // given - Mock 설정: findAll 호출 시 빈 리스트 반환
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // when - 역할 목록 조회
        List<RoleResponse> roleResponses = roleService.getAllRoles();

        // then - 결과 검증
        assertThat(roleResponses).isNotNull();
        assertThat(roleResponses).isEmpty(); // 빈 리스트인지 검증

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("역할 정보 수정 성공")
    void updateRole_Success() {
        // given - Mock 설정 및 테스트 데이터 준비
        String existingRoleId = roleAdmin.getRoleId(); // 수정 대상: roleAdmin
        // roleUpdateRequestAdmin 은 @BeforeEach setUp()에서 이미 준비됨

        // findById가 호출되면 수정 대상 roleAdmin 객체를 Optional에 담아 반환 (spy로 감싸서 메서드 호출 검증)
        Role spiedRoleAdmin = spy(roleAdmin);
        when(roleRepository.findById(existingRoleId)).thenReturn(Optional.of(spiedRoleAdmin));

        // when - 서비스 메서드 호출
        RoleResponse updatedRoleResponse = roleService.updateRole(existingRoleId, roleUpdateRequestAdmin);

        // then - 결과 검증
        // 1. 반환된 RoleResponse가 null이 아닌지 확인
        assertThat(updatedRoleResponse).isNotNull();
        // 2. 반환된 RoleResponse의 내용이 roleUpdateRequestAdmin의 내용과 일치하는지 확인
        assertThat(updatedRoleResponse.getRoleId()).isEqualTo(existingRoleId); // ID는 변경되지 않음
        assertThat(updatedRoleResponse.getRoleName()).isEqualTo(roleUpdateRequestAdmin.getRoleName());
        assertThat(updatedRoleResponse.getRoleDescription()).isEqualTo(roleUpdateRequestAdmin.getRoleDescription());

        // 3. spiedRoleAdmin 객체의 updateRoleDetails 메서드가 올바른 인자로 호출되었는지 검증
        verify(spiedRoleAdmin, times(1)).updateRoleDetails(
                roleUpdateRequestAdmin.getRoleName(),
                roleUpdateRequestAdmin.getRoleDescription()
        );
        // 4. JPA 변경 감지로 동작하므로, roleRepository.save()는 호출되지 않음을 확인 (선택적)
        verify(roleRepository, never()).save(any(Role.class));
        // 5. findById는 1번 호출됨
        verify(roleRepository, times(1)).findById(existingRoleId);
    }

    @Test
    @DisplayName("역할 정보 수정 실패 - 존재하지 않는 역할 ID")
    void updateRole_Fail_RoleIdNotFound() {
        // given - Mock 설정
        String nonExistingRoleId = "ROLE_NON_EXISTING";
        // roleUpdateRequestAdmin 은 @BeforeEach setUp()에서 이미 준비됨
        when(roleRepository.findById(nonExistingRoleId)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> roleService.updateRole(nonExistingRoleId, roleUpdateRequestAdmin))
                .isInstanceOf(NotExistRoleException.class)
                .hasMessageContaining("해당 권한이 존재하지 않습니다: ID " + nonExistingRoleId);

        // then (추가 검증)
        verify(roleRepository, times(1)).findById(nonExistingRoleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("역할 삭제 성공")
    void deleteRole_Success() {
        // given - Mock 설정
        String existingRoleId = roleUser.getRoleId(); // 삭제 대상: roleUser

        // findById가 호출되면 삭제 대상 roleUser 객체를 Optional에 담아 반환
        when(roleRepository.findById(existingRoleId)).thenReturn(Optional.of(roleUser));
        // roleRepository.delete()는 void 메서드이므로, 호출 시 아무 일도 하지 않도록 설정 (doNothing)
        doNothing().when(roleRepository).delete(roleUser);

        // when - 서비스 메서드 호출
        roleService.deleteRole(existingRoleId);

        // then - 검증
        // 1. findById가 1번 호출되었는지 확인
        verify(roleRepository, times(1)).findById(existingRoleId);
        // 2. roleRepository.delete()가 올바른 Role 객체로 1번 호출되었는지 확인
        verify(roleRepository, times(1)).delete(roleUser);
    }

    @Test
    @DisplayName("역할 삭제 실패 - 존재하지 않는 역할 ID")
    void deleteRole_Fail_RoleIdNotFound() {
        // given - Mock 설정
        String nonExistingRoleId = "ROLE_NON_EXISTING";
        when(roleRepository.findById(nonExistingRoleId)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> roleService.deleteRole(nonExistingRoleId))
                .isInstanceOf(NotExistRoleException.class)
                .hasMessageContaining("해당 권한이 존재하지 않습니다: ID " + nonExistingRoleId);

        // then (추가 검증)
        verify(roleRepository, times(1)).findById(nonExistingRoleId);
        verify(roleRepository, never()).delete(any(Role.class));
    }
}

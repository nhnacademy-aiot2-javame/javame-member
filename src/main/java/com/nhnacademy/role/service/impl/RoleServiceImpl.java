package com.nhnacademy.role.service.impl;

import com.nhnacademy.role.common.AlreadyExistRoleException;
import com.nhnacademy.role.common.NotExistRoleException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.request.RoleRegisterRequest;
import com.nhnacademy.role.dto.request.RoleUpdateRequest;
import com.nhnacademy.role.dto.response.RoleResponse;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.role.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RoleService} 인터페이스의 구현 클래스입니다.
 * 역할(Role) 데이터에 대한 생성(Create), 조회(Read), 수정(Update), 삭제(Delete)
 * 비즈니스 로직을 담당합니다. 모든 작업은 기본적으로 트랜잭션 내에서 수행됩니다.
 * 역할 생성/수정/삭제 기능의 실제 API 노출 여부는 신중하게 결정해야 합니다.
 *
 * @see com.nhnacademy.role.service.RoleService
 * @see com.nhnacademy.role.domain.Role
 * @see com.nhnacademy.role.repository.RoleRepository
 */
@Service
@RequiredArgsConstructor // final 필드 생성자 주입
@Transactional         // 클래스 레벨 기본 트랜잭션 (메서드별 재정의 가능)
@Slf4j                 // Lombok 로깅
public class RoleServiceImpl implements RoleService {

    /** 역할 데이터 접근을 위한 리포지토리 */
    private final RoleRepository roleRepository;

    /**
     * {@inheritDoc}
     * 이 구현체는 먼저 {@link RoleRepository#existsById(Object)}를 사용하여 요청된 역할 ID의 중복 여부를 확인합니다.
     * 중복 시 {@link AlreadyExistRoleException}을 발생시킵니다.
     * 중복되지 않으면 {@link Role} 엔티티를 생성하고 {@link RoleRepository#save(Object)}를 호출하여 데이터베이스에 저장합니다.
     * 저장 성공 후, {@link #mapToRoleResponse(Role)} 헬퍼 메서드를 통해 {@link RoleResponse} DTO로 변환하여 반환합니다.
     */
    @Override
    public RoleResponse registerRole(RoleRegisterRequest request) {
        log.debug("역할 등록 요청 시작: ID {}", request.getRoleId());

        // 역할 중복 체크
        if (roleRepository.existsById(request.getRoleId())) {
            log.warn("역할 등록 실패: 이미 존재하는 역할 ID {}", request.getRoleId());
            throw new AlreadyExistRoleException("해당 권한 ID는 이미 존재합니다: " + request.getRoleId());
        }

        // 엔티티 생성 및 저장
        Role newRole = new Role(request.getRoleId(), request.getRoleName(), request.getRoleDescription());
        Role savedRole = roleRepository.save(newRole);
        log.info("역할 등록 완료: ID {}", savedRole.getRoleId());

        // DTO 변환 및 반환
        return mapToRoleResponse(savedRole);
    }

    /**
     * {@inheritDoc}
     * 이 구현체는 {@code @Transactional(readOnly = true)}로 설정되어 읽기 전용 트랜잭션으로 실행됩니다.
     * 내부적으로 {@link #findRoleByIdOrThrow(String)} 헬퍼 메서드를 호출하여 역할을 조회합니다.
     * 조회 성공 시, {@link #mapToRoleResponse(Role)} 헬퍼 메서드를 통해 {@link RoleResponse} DTO로 변환하여 반환합니다.
     * 해당 ID의 역할이 없으면 {@link NotExistRoleException}이 발생합니다.
     */
    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public RoleResponse getRoleById(String roleId) {
        log.debug("역할 정보 조회 요청: ID {}", roleId);
        Role role = findRoleByIdOrThrow(roleId); // 헬퍼 메서드 사용
        log.debug("역할 정보 조회 성공: ID {}", roleId);
        return mapToRoleResponse(role);
    }

    /**
     * {@inheritDoc}
     * 이 구현체는 {@code @Transactional(readOnly = true)}로 설정되어 읽기 전용 트랜잭션으로 실행됩니다.
     * {@link RoleRepository#findAll()}을 호출하여 모든 역할 엔티티를 조회한 후,
     * Stream API와 {@link #mapToRoleResponse(Role)} 헬퍼 메서드를 사용하여 {@link RoleResponse} DTO 리스트로 변환하여 반환합니다.
     */
    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public List<RoleResponse> getAllRoles() {
        log.debug("모든 역할 목록 조회 요청");
        List<Role> roles = roleRepository.findAll();
        log.debug("총 {}개의 역할 조회됨.", roles.size());
        // Stream을 사용하여 DTO 리스트로 변환
        return roles.stream()
                .map(this::mapToRoleResponse) // mapToRoleResponse 메서드 참조
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * 이 구현체는 내부적으로 {@link #findRoleByIdOrThrow(String)} 헬퍼 메서드를 호출하여 수정 대상 역할을 조회합니다.
     * 조회 성공 시, {@link Role#updateRoleDetails(String, String)} 메서드를 호출하여 엔티티의 상태(이름, 설명)를 변경합니다.
     * JPA의 변경 감지(Dirty Checking) 메커니즘에 의해 트랜잭션 커밋 시 데이터베이스에 UPDATE 쿼리가 자동으로 실행됩니다.
     * (별도의 {@code save()} 호출은 필요하지 않습니다.)
     * 수정 완료 후, {@link #mapToRoleResponse(Role)} 헬퍼 메서드를 통해 변경된 정보가 반영된 {@link RoleResponse} DTO를 반환합니다.
     * 해당 ID의 역할이 없으면 {@link NotExistRoleException}이 발생합니다.
     */
    @Override
    public RoleResponse updateRole(String roleId, RoleUpdateRequest request) {
        log.debug("역할 정보 수정 요청: ID {}", roleId);
        Role role = findRoleByIdOrThrow(roleId); // 헬퍼 메서드 사용

        // 엔티티의 상태 변경 메서드 호출
        role.updateRoleDetails(request.getRoleName(), request.getRoleDescription());
        log.info("역할 정보 수정 완료: ID {}", roleId);

        // 변경 감지로 자동 업데이트, save() 불필요
        return mapToRoleResponse(role);
    }

    /**
     * {@inheritDoc}
     * 이 구현체는 내부적으로 {@link #findRoleByIdOrThrow(String)} 헬퍼 메서드를 호출하여 삭제 대상 역할을 먼저 조회합니다.
     * 조회 성공 시, {@link RoleRepository#delete(Object)} 메서드를 호출하여 데이터베이스에서 해당 역할 레코드를 물리적으로 삭제합니다.
     * (주의: 이 역할을 참조하는 다른 엔티티(예: Member)가 있는 경우, 외래 키 제약 조건 위반으로 인해 삭제가 실패할 수 있습니다.
     * 삭제 전에 해당 역할을 사용하는 엔티티가 없는지 확인하거나, 관련 엔티티의 역할 정보를 null로 변경하는 등의 사전 처리가 필요할 수 있습니다.)
     * 해당 ID의 역할이 없으면 {@link NotExistRoleException}이 발생합니다.
     */
    @Override
    public void deleteRole(String roleId) {
        log.debug("역할 삭제 요청: ID {}", roleId);
        Role roleToDelete = findRoleByIdOrThrow(roleId); // 존재 여부 확인 및 엔티티 조회
        roleRepository.delete(roleToDelete); // 엔티티 객체로 삭제 수행
        log.info("역할 삭제 완료: ID {}", roleId);
    }

    // --- Helper Methods ---

    /**
     * 주어진 ID로 {@link Role}을 조회하고, 없으면 {@link NotExistRoleException}을 발생시키는 내부 헬퍼 메서드.
     * 서비스 내 여러 메서드에서 역할 조회 및 예외 처리 로직의 중복을 방지합니다.
     *
     * @param roleId 조회할 역할의 고유 ID
     * @return 조회된 {@link Role} 엔티티
     * @throws NotExistRoleException 해당 ID의 역할이 존재하지 않을 경우
     */
    private Role findRoleByIdOrThrow(String roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.warn("내부 조회 실패: 존재하지 않는 역할 ID {}", roleId);
                    return new NotExistRoleException("해당 권한이 존재하지 않습니다: ID " + roleId);
                });
    }

    /**
     * {@link Role} 엔티티 객체를 {@link RoleResponse} DTO 객체로 변환하는 내부 헬퍼 메서드.
     * 엔티티의 필드 값을 DTO의 해당 필드로 매핑합니다.
     *
     * @param role 변환할 {@link Role} 엔티티 객체
     * @return 변환된 {@link RoleResponse} DTO 객체
     */
    private RoleResponse mapToRoleResponse(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getRoleName(),
                role.getRoleDescription()
        );
    }
}

package com.nhnacademy.role.service;

import com.nhnacademy.role.dto.request.RoleRegisterRequest;
import com.nhnacademy.role.dto.request.RoleUpdateRequest;
import com.nhnacademy.role.dto.response.RoleResponse;
import com.nhnacademy.role.common.AlreadyExistRoleException;
import com.nhnacademy.role.common.NotExistRoleException;

import java.util.List;

/**
 * 역할(Role) 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 * 역할 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
public interface RoleService {

    /**
     * 새로운 역할을 시스템에 등록합니다.
     * 역할 ID는 시스템 내에서 고유해야 합니다.
     *
     * @param request 역할 생성에 필요한 정보 (ID, 이름, 설명)가 담긴 {@link RoleRegisterRequest} DTO
     * @return 등록 완료된 역할의 정보 ({@link RoleResponse})
     * @throws AlreadyExistRoleException 요청된 역할 ID가 이미 존재할 경우 (구체 예외 타입 명시)
     */
    RoleResponse registerRole(RoleRegisterRequest request);

    /**
     * 역할 ID를 사용하여 특정 역할의 상세 정보를 조회합니다.
     *
     * @param roleId 조회할 역할의 고유 ID
     * @return 조회된 역할의 정보 ({@link RoleResponse})
     * @throws NotExistRoleException 해당 ID를 가진 역할을 찾을 수 없을 경우 (구체 예외 타입 명시)
     */
    RoleResponse getRoleById(String roleId);

    /**
     * 시스템에 등록된 모든 역할의 목록을 조회합니다.
     *
     * @return 모든 역할 정보 목록 ({@link RoleResponse})
     */
    List<RoleResponse> getAllRoles();

    /**
     * 기존 역할의 정보(이름, 설명)를 수정합니다.
     * 역할 ID(PK)는 변경할 수 없습니다.
     *
     * @param roleId  수정할 역할의 고유 ID
     * @param request 수정할 정보(새 이름, 새 설명)가 담긴 {@link RoleUpdateRequest} DTO
     * @return 정보가 수정된 후의 역할 정보 ({@link RoleResponse})
     * @throws NotExistRoleException 해당 ID를 가진 역할을 찾을 수 없을 경우 (구체 예외 타입 명시)
     */
    RoleResponse updateRole(String roleId, RoleUpdateRequest request);

    /**
     * 역할 ID를 사용하여 특정 역할을 시스템에서 삭제합니다.
     *
     * @param roleId 삭제할 역할의 고유 ID
     * @throws NotExistRoleException 해당 ID를 가진 역할을 찾을 수 없을 경우 (구체 예외 타입 명시)
     */
    void deleteRole(String roleId);
}

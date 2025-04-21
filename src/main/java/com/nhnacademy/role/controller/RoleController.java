package com.nhnacademy.role.controller;

import com.nhnacademy.role.dto.request.RoleRegisterRequest;
import com.nhnacademy.role.dto.request.RoleUpdateRequest;
import com.nhnacademy.role.dto.response.RoleResponse;
import com.nhnacademy.role.service.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 역할(Role) 관련 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * 역할 조회, 생성, 수정, 삭제 등의 API 엔드포인트를 제공합니다.
 * TODO: 역할 생성/수정/삭제 API는 보안상 민감할 수 있으므로 접근 제어가 필요합니다.
 * 모든 경로는 "/api/v1/roles"를 기본으로 합니다.
 */
@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE) // 기본 경로 및 JSON 형태 응답 타입
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 새로운 역할을 시스템에 등록합니다. (관리자 기능 등 제한된 사용 권장)
     * 성공 시 HTTP 상태 코드 201 (Created)과 생성된 역할 정보를 반환합니다.
     *
     * @param request 역할 생성 정보 DTO ({@link RoleRegisterRequest})
     * @return 생성된 역할 정보 ({@link RoleResponse})와 상태 코드 201
     */
    @PostMapping
    public ResponseEntity<RoleResponse> registerRole(
            @Validated @RequestBody RoleRegisterRequest request) {
        RoleResponse response = roleService.registerRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주어진 역할 ID에 해당하는 역할 정보를 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 역할 정보를 반환합니다.
     *
     * @param roleId 조회할 역할의 ID (경로 변수)
     * @return 조회된 역할 정보 ({@link RoleResponse})와 상태 코드 200
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponse> getRoleById(
            @PathVariable String roleId) {
        RoleResponse response = roleService.getRoleById(roleId);
        return ResponseEntity.ok(response);
    }

    /**
     * 시스템에 등록된 모든 역할 목록을 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 역할 정보 리스트를 반환합니다.
     *
     * @return 모든 역할 정보 리스트 ({@link RoleResponse})와 상태 코드 200
     */
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 역할 ID에 해당하는 역할의 정보(이름, 설명)를 수정합니다. (관리자 기능 등 제한된 사용 권장)
     * 성공 시 HTTP 상태 코드 200 (OK)과 수정된 역할 정보를 반환합니다.
     *
     * @param roleId  수정할 역할의 ID (경로 변수)
     * @param request 수정할 정보 DTO ({@link RoleUpdateRequest})
     * @return 수정된 역할 정보 ({@link RoleResponse})와 상태 코드 200
     */
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable String roleId,
            @Validated @RequestBody RoleUpdateRequest request) {
        RoleResponse response = roleService.updateRole(roleId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 역할 ID에 해당하는 역할을 시스템에서 삭제합니다. (관리자 기능 등 제한된 사용 및 주의 필요)
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다.
     * (주의: 해당 역할을 사용하는 회원이 있을 경우 삭제가 실패할 수 있습니다.)
     *
     * @param roleId 삭제할 역할의 ID (경로 변수)
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable String roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}

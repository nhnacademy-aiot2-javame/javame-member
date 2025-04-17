package com.nhnacademy.role.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 역할(Role) 정보 조회 응답 시 반환될 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    /**
     * 역할의 고유 ID (예: "ROLE_USER").
     */
    private String roleId;

    /**
     * 역할의 이름 (예: "일반 사용자").
     */
    private String roleName;

    /**
     * 역할에 대한 설명.
     */
    private String roleDescription;

}

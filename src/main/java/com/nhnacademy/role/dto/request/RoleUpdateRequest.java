package com.nhnacademy.role.dto.request; // request 패키지 사용

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기존 역할(Role)의 정보를 수정할 때 필요한 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    /**
     * 변경할 역할의 새 이름.
     */
    private String roleName;

    /**
     * 변경할 역할의 새 설명.
     */
    private String roleDescription;

}

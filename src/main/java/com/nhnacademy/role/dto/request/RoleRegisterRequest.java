package com.nhnacademy.role.dto.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 새로운 역할(Role) 생성을 요청할 때 필요한 데이터를 담는 DTO 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleRegisterRequest {

    /**
     * 생성할 역할의 고유 ID (예: "ROLE_MANAGER").
     */
    private String roleId;

    /**
     * 생성할 역할의 이름 (예: "매니저").
     */
    private String roleName;

    /**
     * 역할에 대한 부가적인 설명
     */
    private String roleDescription;

}

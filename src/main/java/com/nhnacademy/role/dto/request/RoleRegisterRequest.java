package com.nhnacademy.role.dto.request;

import jakarta.validation.constraints.NotBlank; // jakarta 패키지 사용
import jakarta.validation.constraints.Size;   // jakarta 패키지 사용
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
     * 필수 값이며, 공백일 수 없습니다.
     */
    @NotBlank(message = "역할 ID는 필수입니다.") // null, 빈 문자열, 공백만 있는 문자열 모두 허용 안 함
    @Size(min = 1, max = 50, message = "역할 ID는 1자 이상 50자 이하이어야 합니다.")
    private String roleId;

    /**
     * 생성할 역할의 이름 (예: "매니저").
     * 필수 값이며, 공백일 수 없습니다.
     */
    @NotBlank(message = "역할 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "역할 이름은 1자 이상 100자 이하이어야 합니다.")
    private String roleName;

    /**
     * 역할에 대한 부가적인 설명.
     * 선택 사항일 수 있으므로 @NotBlank 대신 @Size만 적용하거나, 아무것도 적용하지 않을 수 있습니다.
     */
    @Size(max = 255, message = "역할 설명은 255자 이하이어야 합니다.")
    private String roleDescription;

}

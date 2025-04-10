package com.nhnacademy.exam.javamememberapi.role.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRegisterRequest {

    @NotBlank(message = "권한아이디는 필수 입력 항목입니다.")
    private final String roleId;

    @NotBlank(message = "권한이름은 필수 입력 항목입니다.")
    private final String roleName;

    private final String roleDescription;

    public RoleRegisterRequest(String roleId, String roleName, String roleDescription) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }
}

package com.nhnacademy.exam.javamememberapi.role.dto;

public class RoleUpdateRequest {

    private final String roleName;

    private final String roleDescription;


    public RoleUpdateRequest(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }
}

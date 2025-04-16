package com.nhnacademy.role.dto;

public class RoleResponse {

    private final String roleName;

    private final String roleId;

    private final String roleDescription;

    public RoleResponse(String roleName, String roleId, String roleDescription) {
        this.roleName = roleName;
        this.roleId = roleId;
        this.roleDescription = roleDescription;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleDescription() {
        return roleDescription;
    }
}

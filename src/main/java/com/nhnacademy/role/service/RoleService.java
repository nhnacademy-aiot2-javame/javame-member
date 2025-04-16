package com.nhnacademy.role.service;

import com.nhnacademy.role.dto.RoleRegisterRequest;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.dto.RoleUpdateRequest;

public interface RoleService {

    RoleResponse registerRole(RoleRegisterRequest roleRegisterRequest);

    RoleResponse getRole(String roleId);

    void deleteRole(String roleId);

    RoleResponse updateRole(String roleId, RoleUpdateRequest roleUpdateRequest);
}

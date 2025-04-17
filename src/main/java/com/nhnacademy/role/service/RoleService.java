package com.nhnacademy.role.service;

import com.nhnacademy.role.dto.request.RoleRegisterRequest;
import com.nhnacademy.role.dto.response.RoleResponse;
import com.nhnacademy.role.dto.request.RoleUpdateRequest;

public interface RoleService {

    RoleResponse registerRole(RoleRegisterRequest roleRegisterRequest);

    RoleResponse getRole(String roleId);

    void deleteRole(String roleId);

    RoleResponse updateRole(String roleId, RoleUpdateRequest roleUpdateRequest);
}

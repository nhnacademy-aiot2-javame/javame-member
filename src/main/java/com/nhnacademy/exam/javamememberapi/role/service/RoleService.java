package com.nhnacademy.exam.javamememberapi.role.service;

import com.nhnacademy.exam.javamememberapi.role.dto.RoleRegisterRequest;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleResponse;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleUpdateRequest;

public interface RoleService {

    RoleResponse registerRole(RoleRegisterRequest roleRegisterRequest);

    RoleResponse getRole(String roleId);

    void deleteRole(String roleId);

    RoleResponse updateRole(String roleId, RoleUpdateRequest roleUpdateRequest);
}

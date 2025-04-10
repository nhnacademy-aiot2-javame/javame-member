package com.nhnacademy.exam.javamememberapi.role.service.Impl;

import com.nhnacademy.exam.javamememberapi.role.common.AlreadyExistRoleException;
import com.nhnacademy.exam.javamememberapi.role.common.NotExistRoleException;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleRegisterRequest;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleResponse;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleUpdateRequest;
import com.nhnacademy.exam.javamememberapi.role.repository.RoleRepository;
import com.nhnacademy.exam.javamememberapi.role.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;


    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }



    private RoleResponse roleResponseMapper(Role role){
        return new RoleResponse(
                role.getRoleName(),
                role.getRoleId(),
                role.getRoleDescription()
                );
    }

    @Override
    public RoleResponse registerRole(RoleRegisterRequest roleRegisterRequest) {
        Boolean isExist = roleRepository.findRoleByRoleId(roleRegisterRequest.getRoleId());
        if (isExist) {
            throw new AlreadyExistRoleException("해당 권한은 이미 존재합니다.");
        }
        Role role = new Role(
                roleRegisterRequest.getRoleId(),
                roleRegisterRequest.getRoleName(),
                roleRegisterRequest.getRoleDescription()
        );
        return roleResponseMapper(role);
    }


    @Override
    public RoleResponse getRole(String roleId) {
        Optional<Role> optionalRole = roleRepository.getRoleByRoleId(roleId);
        if(!optionalRole.isPresent()){
            throw new NotExistRoleException("해당 권한이 존재하지 않습니다.");
        }
        Role role = optionalRole.get();

        return roleResponseMapper(role);
    }

    @Override
    public void deleteRole(String roleId) {
        Optional<Role> optionalRole = roleRepository.getRoleByRoleId(roleId);
        if(optionalRole.isEmpty()){
            throw new NotExistRoleException("해당 권한이 존재하지 않습니다.");
        }
        Role deleteTargetRole = optionalRole.get();
        roleRepository.delete(deleteTargetRole);

    }

    @Override
    public RoleResponse updateRole(String roleId, RoleUpdateRequest roleUpdateRequest) {
        Role role = roleRepository.findById(roleId).orElseThrow(()->new NotExistRoleException("Role이 없습니다."));
//        role.updateRole(roleUpdateRequest.getRoleName(), roleUpdateRequest.get);

        return null;
    }



}

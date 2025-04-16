package com.nhnacademy.role.service.Impl;

import com.nhnacademy.role.common.AlreadyExistRoleException;
import com.nhnacademy.role.common.NotExistRoleException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.dto.RoleRegisterRequest;
import com.nhnacademy.role.dto.RoleResponse;
import com.nhnacademy.role.dto.RoleUpdateRequest;
import com.nhnacademy.role.repository.RoleRepository;
import com.nhnacademy.role.service.RoleService;
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
        Boolean isExist = roleRepository.existsRoleByRoleId(roleRegisterRequest.getRoleId());
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
        Optional<Role> optionalRole = roleRepository.findRoleByRoleId(roleId);
        if(!optionalRole.isPresent()){
            throw new NotExistRoleException("해당 권한이 존재하지 않습니다.");
        }
        Role role = optionalRole.get();

        return roleResponseMapper(role);
    }

    @Override
    public void deleteRole(String roleId) {
        Optional<Role> optionalRole = roleRepository.findRoleByRoleId(roleId);
        if(optionalRole.isEmpty()){
            throw new NotExistRoleException("해당 권한이 존재하지 않습니다.");
        }
        Role deleteTargetRole = optionalRole.get();
        roleRepository.delete(deleteTargetRole);

    }

    @Override
    public RoleResponse updateRole(String roleId, RoleUpdateRequest roleUpdateRequest) {
        Optional<Role> optionalRole = roleRepository.findRoleByRoleId(roleId);
        if(optionalRole.isEmpty()){
            throw new NotExistRoleException("존재하지 않는 권한입니다.");
        }
        Role updateTarget = optionalRole.get();
        roleRepository.save(updateTarget);

        return roleResponseMapper(updateTarget);
    }



}

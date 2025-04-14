package com.nhnacademy.exam.javamememberapi.role;

import com.google.common.util.concurrent.MoreExecutors;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleRegisterRequest;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleResponse;
import com.nhnacademy.exam.javamememberapi.role.dto.RoleUpdateRequest;
import com.nhnacademy.exam.javamememberapi.role.repository.RoleRepository;
import com.nhnacademy.exam.javamememberapi.role.service.Impl.RoleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    @Test
    @DisplayName("register Role Test")
    void registerRoleTest(){
        RoleRegisterRequest roleRegisterRequest =
                new RoleRegisterRequest("ROLE_ADMIN", "ADMIN", "This is admin");
        Mockito.when(roleRepository.existsRoleByRoleId(Mockito.anyString())).thenReturn(false);

        RoleResponse response = roleService.registerRole(roleRegisterRequest);

        Assertions.assertNotNull(response);
        Assertions.assertAll(
                ()->Assertions.assertEquals("ROLE_ADMIN", response.getRoleId()),
                ()->Assertions.assertEquals("ADMIN", response.getRoleName()),
                ()->Assertions.assertEquals("This is admin", response.getRoleDescription())
        );
    }

    @Test
    @DisplayName("get Role Test")
    void getRoleTest(){
        Role role = new Role("ROLE_ADMIN", "ADMIN", "This is admin");
        Mockito.when(roleRepository.findRoleByRoleId(Mockito.anyString())).thenReturn(Optional.of(role));
        RoleResponse response = roleService.getRole("ROLE_ADMIN");
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                ()->Assertions.assertEquals("ROLE_ADMIN", response.getRoleId()),
                ()->Assertions.assertEquals("ADMIN", response.getRoleName()),
                ()->Assertions.assertEquals("This is admin", response.getRoleDescription())
        );
    }

    @Test
    @DisplayName("update Role Test")
    void updateRoleTest(){
        Role role = new Role("ROLE_ADMIN", "ADMIN", "This is admin");
        RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("USER", "This is User");

        Mockito.when(roleRepository.findRoleByRoleId(Mockito.anyString())).thenReturn(Optional.of(role));

        RoleResponse response = roleService.updateRole("ROLE_ADMIN",roleUpdateRequest);

        Assertions.assertNotNull(response);
        Assertions.assertAll(
                ()->Assertions.assertEquals("ROLE_ADMIN", response.getRoleId()),
                ()->Assertions.assertEquals("USER", response.getRoleName()),
                ()->Assertions.assertEquals("This is User", response.getRoleDescription())
        );
    }

    @Test
    @DisplayName("Update Role Test")
    void deleteRoleTest(){
        Role role = new Role("ROLE_ADMIN", "ADMIN", "This is admin");

        Mockito.when(roleRepository.findRoleByRoleId(Mockito.anyString())).thenReturn(Optional.of(role));

        roleService.deleteRole("ROLE_ADMIN");

        Mockito.verify(roleRepository,Mockito.times(1)).findRoleByRoleId(Mockito.anyString());
        Mockito.verify(roleRepository,Mockito.times(1)).delete(Mockito.any());
    }

}

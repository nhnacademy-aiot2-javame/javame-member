package com.nhnacademy;

import com.nhnacademy.member.service.MemberService;
import com.nhnacademy.role.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = RoleController.class)
class RoleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RoleService roleService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void registerRole() {
    }

    @Test
    void getRoleById() {
    }

    @Test
    void getAllRoles() {
    }

    @Test
    void updateRole() {
    }

    @Test
    void deleteRole() {
    }
}
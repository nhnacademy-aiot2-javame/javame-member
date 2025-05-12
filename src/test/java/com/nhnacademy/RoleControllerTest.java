package com.nhnacademy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // 추가
import com.nhnacademy.common.exception.GlobalExceptionHandler; // 추가
import com.nhnacademy.common.exception.ResourceAlreadyExistsException; // 추가
import com.nhnacademy.common.exception.ResourceNotFoundException;   // 추가
import com.nhnacademy.role.dto.request.RoleRegisterRequest;
import com.nhnacademy.role.dto.request.RoleUpdateRequest;
import com.nhnacademy.role.dto.response.RoleResponse;
import com.nhnacademy.role.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest; // 추가
import org.junit.jupiter.params.provider.ValueSource; // 추가
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import; // 추가
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder; // 추가
import static org.hamcrest.Matchers.hasSize; // 추가
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // 추가 (디버깅용)
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoleController.class)
@Import(GlobalExceptionHandler.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    private ObjectMapper objectMapper;

    // 테스트에 사용될 역할 ID 목록
    private static final List<String> ROLE_IDS = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER");
    private RoleRegisterRequest defaultRegisterRequest;
    private RoleResponse defaultRoleResponse;
    private RoleUpdateRequest defaultUpdateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // LocalDateTime 직렬화/역직렬화를 위해 JavaTimeModule 등록
        objectMapper.registerModule(new JavaTimeModule());

        // 공통으로 사용될 DTO 객체 초기화
        defaultRegisterRequest = new RoleRegisterRequest("ROLE_NEW", "New Role", "새로운 역할 설명");
        defaultRoleResponse = createRoleResponse("ROLE_NEW", "New Role", "새로운 역할 설명");
        defaultUpdateRequest = new RoleUpdateRequest("Updated Role Name", "업데이트된 역할 설명");
    }

    // 공통 응답 생성 헬퍼 메서드
    private RoleResponse createRoleResponse(String roleId, String name, String desc) {
        return new RoleResponse(roleId, name, desc);
    }

    // --- 역할 등록 테스트 ---
    @Test
    @DisplayName("역할 등록 성공 - 새로운 역할 생성")
    void registerRole_Success_NewRole() throws Exception {
        when(roleService.registerRole(any(RoleRegisterRequest.class))).thenReturn(defaultRoleResponse);

        performPostRequest("/roles", defaultRegisterRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roleId").value(defaultRoleResponse.getRoleId()))
                .andExpect(jsonPath("$.roleName").value(defaultRoleResponse.getRoleName()))
                .andExpect(jsonPath("$.roleDescription").value(defaultRoleResponse.getRoleDescription()));

        verify(roleService, times(1)).registerRole(any(RoleRegisterRequest.class));
    }

    @Test
    @DisplayName("역할 등록 실패 - 이미 존재하는 역할 ID")
    void registerRole_Fail_AlreadyExists() throws Exception {
        RoleRegisterRequest duplicateRequest = new RoleRegisterRequest("ROLE_USER", "User", "일반 사용자");
        String errorMessage = "해당 권한 ID는 이미 존재합니다: " + duplicateRequest.getRoleId();
        when(roleService.registerRole(any(RoleRegisterRequest.class)))
                .thenThrow(new ResourceAlreadyExistsException(errorMessage)); // GlobalExceptionHandler에서 처리될 커스텀 예외

        performPostRequest("/roles", duplicateRequest)
                .andExpect(status().isConflict()) // GlobalExceptionHandler에 의해 409로 매핑됨
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roleService, times(1)).registerRole(any(RoleRegisterRequest.class));
    }

    @Test
    @DisplayName("역할 등록 실패 - 요청 DTO 유효성 검증 실패 (roleId가 null)")
    void registerRole_Fail_Validation_NullRoleId() throws Exception {
        RoleRegisterRequest invalidRequest = new RoleRegisterRequest(null, "Valid Name", "Valid Desc");

        performPostRequest("/roles", invalidRequest)
                .andDo(print()) // 응답 로깅 (디버깅 시 유용)
                .andExpect(status().isBadRequest()) // @Validated에 의해 MethodArgumentNotValidException 발생
                .andExpect(jsonPath("$.message").exists()); // GlobalExceptionHandler의 메시지 포맷 확인
    }

    // --- 단일 역할 조회 테스트 ---
    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER"})
    @DisplayName("역할 ID로 역할 조회 성공 - 매개변수화된 테스트")
    void getRoleById_Success_Parameterized(String roleId) throws Exception {
        RoleResponse expectedResponse = createRoleResponse(roleId, roleId.substring(5), "테스트 설명 " + roleId);
        when(roleService.getRoleById(roleId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/roles/{roleId}", roleId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roleId").value(expectedResponse.getRoleId()))
                .andExpect(jsonPath("$.roleName").value(expectedResponse.getRoleName()))
                .andExpect(jsonPath("$.roleDescription").value(expectedResponse.getRoleDescription()));

        verify(roleService, times(1)).getRoleById(roleId);
    }

    @Test
    @DisplayName("역할 ID로 역할 조회 실패 - 존재하지 않는 역할 ID")
    void getRoleById_Fail_NotFound() throws Exception {
        String nonExistingRoleId = "ROLE_NON_EXISTENT";
        String errorMessage = "해당 권한이 존재하지 않습니다: ID " + nonExistingRoleId;
        when(roleService.getRoleById(nonExistingRoleId)).thenThrow(new ResourceNotFoundException(errorMessage));

        mockMvc.perform(get("/roles/{roleId}", nonExistingRoleId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // GlobalExceptionHandler에 의해 404로 매핑됨
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roleService, times(1)).getRoleById(nonExistingRoleId);
    }

    // --- 모든 역할 조회 테스트 ---
    @Test
    @DisplayName("모든 역할 조회 성공 - 역할 목록 반환")
    void getAllRoles_Success_ReturnsListOfRoles() throws Exception {
        List<RoleResponse> expectedResponses = ROLE_IDS.stream()
                .map(id -> createRoleResponse(id, id.substring(5), "설명 " + id))
                .toList();
        when(roleService.getAllRoles()).thenReturn(expectedResponses);

        mockMvc.perform(get("/roles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(ROLE_IDS.size())))
                .andExpect(jsonPath("$[*].roleId", containsInAnyOrder(ROLE_IDS.toArray(new String[0]))));

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    @DisplayName("모든 역할 조회 성공 - 역할 없을 시 빈 목록 반환")
    void getAllRoles_Success_ReturnsEmptyListWhenNoRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/roles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(roleService, times(1)).getAllRoles();
    }

    // --- 역할 수정 테스트 ---
    @Test
    @DisplayName("역할 정보 수정 성공")
    void updateRole_Success() throws Exception {
        String targetRoleId = "ROLE_USER";
        RoleResponse updatedResponse = createRoleResponse(targetRoleId, defaultUpdateRequest.getRoleName(), defaultUpdateRequest.getRoleDescription());
        when(roleService.updateRole(eq(targetRoleId), any(RoleUpdateRequest.class))).thenReturn(updatedResponse);

        performPutRequest("/roles/{roleId}", targetRoleId, defaultUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roleId").value(targetRoleId))
                .andExpect(jsonPath("$.roleName").value(defaultUpdateRequest.getRoleName()))
                .andExpect(jsonPath("$.roleDescription").value(defaultUpdateRequest.getRoleDescription()));

        verify(roleService, times(1)).updateRole(eq(targetRoleId), any(RoleUpdateRequest.class));
    }

    @Test
    @DisplayName("역할 정보 수정 실패 - 존재하지 않는 역할 ID")
    void updateRole_Fail_NotFound() throws Exception {
        String nonExistingRoleId = "ROLE_NON_EXISTENT";
        String errorMessage = "해당 권한이 존재하지 않습니다: ID " + nonExistingRoleId;
        when(roleService.updateRole(eq(nonExistingRoleId), any(RoleUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        performPutRequest("/roles/{roleId}", nonExistingRoleId, defaultUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roleService, times(1)).updateRole(eq(nonExistingRoleId), any(RoleUpdateRequest.class));
    }

    // --- 역할 삭제 테스트 ---
    @Test
    @DisplayName("역할 삭제 성공")
    void deleteRole_Success() throws Exception {
        String targetRoleId = "ROLE_ADMIN";
        doNothing().when(roleService).deleteRole(targetRoleId);

        mockMvc.perform(delete("/roles/{roleId}", targetRoleId))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).deleteRole(targetRoleId);
    }

    @Test
    @DisplayName("역할 삭제 실패 - 존재하지 않는 역할 ID")
    void deleteRole_Fail_NotFound() throws Exception {
        String nonExistingRoleId = "ROLE_NON_EXISTENT";
        String errorMessage = "해당 권한이 존재하지 않습니다: ID " + nonExistingRoleId;
        doThrow(new ResourceNotFoundException(errorMessage)).when(roleService).deleteRole(nonExistingRoleId);

        mockMvc.perform(delete("/roles/{roleId}", nonExistingRoleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(roleService, times(1)).deleteRole(nonExistingRoleId);
    }

    // --- 유틸리티 메서드 ---
    private ResultActions performPostRequest(String urlTemplate, Object content) throws Exception {
        return mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performPutRequest(String urlTemplate, Object pathVar, Object content) throws Exception {
        return mockMvc.perform(put(urlTemplate, pathVar)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }
}

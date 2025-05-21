package com.nhnacademy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.common.exception.GlobalExceptionHandler;
import com.nhnacademy.common.exception.ResourceAlreadyExistsException;
import com.nhnacademy.common.exception.ResourceNotFoundException;
import com.nhnacademy.company.dto.request.CompanyRegisterRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateEmailRequest;
import com.nhnacademy.company.dto.request.CompanyUpdateRequest;
import com.nhnacademy.company.dto.response.CompanyResponse;
import com.nhnacademy.company.service.CompanyService;
import com.nhnacademy.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class}) // 전역 예외 핸들러 포함
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean // CompanyController가 MemberService도 의존하므로 MockBean으로 추가
    private MemberService memberService;

    private ObjectMapper objectMapper;

    // 테스트에 사용될 기본 객체들
    private CompanyRegisterRequest defaultRegisterRequest;
    private CompanyUpdateRequest defaultUpdateRequest;
    private CompanyUpdateEmailRequest defaultUpdateEmailRequest;
    private CompanyResponse defaultCompanyResponse;

    private final String BASE_URL = "/companies";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        defaultRegisterRequest = new CompanyRegisterRequest(
                "nhn.com", "NHN", "nhn@nhn.com", "031-000-0000", "Pangyo"
        );
        defaultUpdateRequest = new CompanyUpdateRequest("NHN Global", "031-111-1111", "New Pangyo");
        defaultUpdateEmailRequest = new CompanyUpdateEmailRequest("nhn@nhn.com", "contact@nhn.com");
        defaultCompanyResponse = new CompanyResponse(
                "nhn.com", "NHN", "nhn@nhn.com", "031-000-0000", "Pangyo", LocalDateTime.now(), true
        );
    }

    // --- 공통 요청 생성 헬퍼 메서드 ---
    private ResultActions performPostRequest(String url, Object content) throws Exception {
        return mockMvc.perform(post(url)
                .header("X-USER-ROLE", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performPutRequest(String url, Object content) throws Exception {
        return mockMvc.perform(put(url)
                .header("X-USER-ROLE", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    private ResultActions performPatchRequest(String url) throws Exception {
        return mockMvc.perform(patch(url)
                .header("X-USER-ROLE", "ROLE_ADMIN")
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetRequest(String url) throws Exception {
        return mockMvc.perform(get(url)
                .header("X-USER-ROLE", "ROLE_ADMIN")
                .accept(MediaType.APPLICATION_JSON));
    }


    // --- 회사 등록 (+Owner 생성) 테스트 ---
    @Test
    @DisplayName("회사 및 Owner 등록 성공")
    void registerCompany_Success() throws Exception {
        when(companyService.registerCompany(any(CompanyRegisterRequest.class))).thenReturn(defaultCompanyResponse);
        // companyService.registerCompany 내부에서 memberService.registerOwner 등이 호출된다고 가정하고,
        // 그 부분은 CompanyService 단위 테스트에서 검증되었을 것으로 가정.
        // Controller 테스트에서는 companyService.registerCompany 호출과 반환값 검증에 집중.

        performPostRequest(BASE_URL + "/register", defaultRegisterRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.companyDomain").value(defaultCompanyResponse.getCompanyDomain()))
                .andExpect(jsonPath("$.companyName").value(defaultCompanyResponse.getCompanyName()));

        verify(companyService, times(1)).registerCompany(any(CompanyRegisterRequest.class));
    }

    @Test
    @DisplayName("회사 등록 실패 - 이미 존재하는 도메인")
    void registerCompany_Fail_AlreadyExists() throws Exception {
        String errorMessage = "이미 사용 중인 회사 도메인입니다: " + defaultRegisterRequest.getCompanyDomain();
        when(companyService.registerCompany(any(CompanyRegisterRequest.class)))
                .thenThrow(new ResourceAlreadyExistsException(errorMessage));

        performPostRequest(BASE_URL + "/register", defaultRegisterRequest)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    // --- 회사 도메인으로 조회 테스트 ---
    @Test
    @DisplayName("회사 도메인으로 조회 성공")
    void getCompanyByDomain_Success() throws Exception {
        String domain = defaultCompanyResponse.getCompanyDomain();
        when(companyService.getCompanyByDomain(domain)).thenReturn(defaultCompanyResponse);

        performGetRequest(BASE_URL + "/" + domain)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyDomain").value(domain))
                .andExpect(jsonPath("$.companyName").value(defaultCompanyResponse.getCompanyName()));
    }

    @Test
    @DisplayName("회사 도메인으로 조회 실패 - 존재하지 않는 도메인")
    void getCompanyByDomain_Fail_NotFound() throws Exception {
        String nonExistingDomain = "notfound.com";
        String errorMessage = "회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain;
        when(companyService.getCompanyByDomain(nonExistingDomain))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        performGetRequest(BASE_URL + "/" + nonExistingDomain)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    // --- 모든 회사 조회 테스트 ---
    @Test
    @DisplayName("모든 회사 조회 성공")
    void getAllCompanies_Success() throws Exception {
        CompanyResponse companyB = new CompanyResponse("google.com", "Google", "contact@google.com", "02-000-0000", "Seoul", LocalDateTime.now(), true);
        List<CompanyResponse> responses = List.of(defaultCompanyResponse, companyB);
        when(companyService.getAllCompanies()).thenReturn(responses);

        performGetRequest(BASE_URL)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].companyDomain", containsInAnyOrder(defaultCompanyResponse.getCompanyDomain(), companyB.getCompanyDomain())));
    }

    @Test
    @DisplayName("모든 회사 조회 성공 - 회사 없음")
    void getAllCompanies_Success_NoCompanies() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(Collections.emptyList());

        performGetRequest(BASE_URL)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- 회사 정보 수정 테스트 ---
    @Test
    @DisplayName("회사 정보 수정 성공")
    void updateCompany_Success() throws Exception {
        String domain = defaultCompanyResponse.getCompanyDomain();
        CompanyResponse updatedResponse = new CompanyResponse(
                domain,
                defaultUpdateRequest.getCompanyName(),
                defaultCompanyResponse.getCompanyEmail(), // Email은 이 메서드에서 안 바뀜
                defaultUpdateRequest.getCompanyMobile(),
                defaultUpdateRequest.getCompanyAddress(),
                defaultCompanyResponse.getRegisteredAt(),
                defaultCompanyResponse.isActive()
        );
        when(companyService.updateCompany(eq(domain), any(CompanyUpdateRequest.class))).thenReturn(updatedResponse);

        performPutRequest(BASE_URL + "/" + domain, defaultUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value(defaultUpdateRequest.getCompanyName()))
                .andExpect(jsonPath("$.companyMobile").value(defaultUpdateRequest.getCompanyMobile()));
    }

    @Test
    @DisplayName("회사 정보 수정 실패 - 존재하지 않는 도메인")
    void updateCompany_Fail_NotFound() throws Exception {
        String nonExistingDomain = "notfound.com";
        String errorMessage = "회사를 찾을 수 없습니다: 도메인 " + nonExistingDomain;
        when(companyService.updateCompany(eq(nonExistingDomain), any(CompanyUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        performPutRequest(BASE_URL + "/" + nonExistingDomain, defaultUpdateRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }


    // --- 회사 이메일 수정 테스트 ---
    @Test
    @DisplayName("회사 이메일 수정 성공")
    void updateCompanyEmail_Success() throws Exception {
        String domain = defaultCompanyResponse.getCompanyDomain();
        CompanyResponse updatedEmailResponse = new CompanyResponse(
                domain,
                defaultCompanyResponse.getCompanyName(),
                defaultUpdateEmailRequest.getNewEmail(), // 변경된 이메일
                defaultCompanyResponse.getCompanyMobile(),
                defaultCompanyResponse.getCompanyAddress(),
                defaultCompanyResponse.getRegisteredAt(),
                defaultCompanyResponse.isActive()
        );
        when(companyService.updateCompanyEmail(eq(domain), any(CompanyUpdateEmailRequest.class))).thenReturn(updatedEmailResponse);

        performPutRequest(BASE_URL + "/" + domain + "/email", defaultUpdateEmailRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyEmail").value(defaultUpdateEmailRequest.getNewEmail()));
    }

    // --- 회사 비활성화 테스트 ---
    @Test
    @DisplayName("회사 비활성화 성공")
    void deactivateCompany_Success() throws Exception {
        String domain = defaultCompanyResponse.getCompanyDomain();
        doNothing().when(companyService).deactivateCompany(domain);

        performPatchRequest(BASE_URL + "/" + domain + "/deactivate")
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).deactivateCompany(domain);
    }

    // --- 회사 활성화 테스트 ---
    @Test
    @DisplayName("회사 활성화 성공")
    void activateCompany_Success() throws Exception {
        String domain = defaultCompanyResponse.getCompanyDomain();
        doNothing().when(companyService).activateCompany(domain);

        performPatchRequest(BASE_URL + "/" + domain + "/activate")
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).activateCompany(domain);
    }
}
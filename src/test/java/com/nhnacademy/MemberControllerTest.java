package com.nhnacademy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.member.common.NotExistMemberException;
import com.nhnacademy.member.dto.request.MemberPasswordChangeRequest;
import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.response.MemberLoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    private ObjectMapper objectMapper;

    private MemberRegisterRequest memberRegisterRequest;

    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        memberRegisterRequest = new MemberRegisterRequest(
                "newbie@test.com",
                "password1234",
                "test-company.com"
        );

        memberResponse = new MemberResponse(
                1L,
                "newbie@test.com",
                "test-company.com",
                "ROLE_MEMBER"
        );
    }

    @Test
    @DisplayName("회원 등록 성공 테스트")
    void registerMember_Success() throws Exception {
        // given
        MemberRegisterRequest request = new MemberRegisterRequest(
                "newbie@test.com",
                "password123",
                "test-comp.com"
        );

        MemberResponse response = new MemberResponse(
                1L,
                "newbie@test.com",
                "test-comp.com",
                "ROLE_USER"
        );

        when(memberService.registerMember(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/members")
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberNo").value(1L));
    }

    @Test
    @DisplayName("오너 회원 등록 성공 테스트")
    void registerOwner_Success() throws Exception {
        // given
        MemberRegisterRequest request = new MemberRegisterRequest(
                "owner@test.com",
                "ownerpassword",
                "test-company.com"
        );

        MemberResponse response = new MemberResponse(
                2L,
                "owner@test.com",
                "test-company.com",
                "ROLE_OWNER"
        );

        // 서비스 계층의 동작을 미리 정의
        when(memberService.registerOwner(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/members/owner")
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberNo").value(2L))
                .andExpect(jsonPath("$.memberEmail").value("owner@test.com"))
                .andExpect(jsonPath("$.companyDomain").value("test-company.com"))
                .andExpect(jsonPath("$.roleId").value("ROLE_OWNER"));
    }



    @Test
    @DisplayName("마지막 로그인 시간 업데이트 성공")
    void updateLastLogin_Success() throws Exception {
        // given
        String email = "user@test.com";
        doNothing().when(memberService).updateLoginAt(email);

        // when & then
        mockMvc.perform(put("/members/{email}/last-login", email)
                        .header("X-USER-ROLE", "ROLE_ADMIN"))
                .andExpect(status().isOk());

        verify(memberService).updateLoginAt(email);
    }

    @Test
    @DisplayName("회원 ID로 조회 성공")
    void getMemberById_Success() throws Exception {
        // given
        Long memberNo = 1L;
        MemberResponse response = new MemberResponse(
                memberNo, "user@test.com", "test-company.com", "ROLE_USER"
        );
        when(memberService.getMemberById(memberNo)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/members/{memberNo}", memberNo)
                        .header("X-USER-ROLE", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberNo").value(memberNo))
                .andExpect(jsonPath("$.memberEmail").value("user@test.com"));
    }

    @Test
    @DisplayName("회원 이메일로 조회 성공")
    void getMemberByEmail_Success() throws Exception {
        // given
        String email = "user@test.com";
        MemberResponse response = new MemberResponse(
                1L, email, "test-company.com", "ROLE_USER"
        );
        when(memberService.getMemberByEmail(email)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/members/member-email/{email}", email)
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .header("X-USER-EMAIL", "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberEmail").value(email));
    }



    @Test
    @DisplayName("비밀번호 변경 성공")
    void changeMemberPassword_Success() throws Exception {
        // given
        Long memberNo = 1L;
        MemberPasswordChangeRequest request =
                new MemberPasswordChangeRequest("oldPass", "newPass");

        doNothing().when(memberService)
                .changeMemberPassword(eq(memberNo), any());
        Mockito.when(memberService.getMemberByEmail(Mockito.anyString())).thenReturn(memberResponse);
        // when & then
        mockMvc.perform(put("/members/{memberNo}/password", memberNo)
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .header("X-USER-EMAIL", "notfound@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteMember_Success() throws Exception {
        // given
        Long memberNo = 1L;
        doNothing().when(memberService).deleteMember(memberNo);
        Mockito.when(memberService.getMemberByEmail(Mockito.anyString())).thenReturn(memberResponse);

        // when & then
        mockMvc.perform(delete("/members/{memberNo}", memberNo)
                        .header("X-USER-EMAIL", "notfound@test.com")
                        .header("X-USER-ROLE", "ROLE_ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인 정보 조회 성공")
    void getLoginInfoByEmail_Success() throws Exception {
        // given
        String email = "user@test.com";
        MemberLoginResponse response =
                new MemberLoginResponse(1L, email, "examplePassword", "ROLE_USER");

        when(memberService.getLoginInfoByEmail(email)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/members/login-info/{email}", email)
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .header("X-USER-EMAIL", "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberEmail").value(email))
                .andExpect(jsonPath("$.roleId").value("ROLE_USER"));
    }

    // 회원 이메일로 조회 실패 - 존재하지 않는 이메일
    @Test
    @DisplayName("회원 이메일로 조회 실패 - 존재하지 않는 이메일")
    void getMemberByEmail_Fail_EmailNotFound() throws Exception {
        // given
        String nonExistingEmail = "notfound@test.com";
        when(memberService.getMemberByEmail(nonExistingEmail))
                .thenThrow(new NotExistMemberException("회원을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/members/member-email/{email}", nonExistingEmail)
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .header("X-USER-EMAIL", "notfound@test.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."));
    }

    // 비밀번호 변경 실패 - 현재 비밀번호 불일치
    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void changeMemberPassword_Fail_PasswordMismatch() throws Exception {
        // given
        Long memberNo = 1L;
        MemberPasswordChangeRequest request =
                new MemberPasswordChangeRequest("wrongPassword", "newPassword");

        doThrow(new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다."))
                .when(memberService).changeMemberPassword(eq(memberNo), any());
        Mockito.when(memberService.getMemberByEmail(Mockito.anyString())).thenReturn(memberResponse);

        // when & then
        mockMvc.perform(put("/members/{memberNo}/password", memberNo)
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .header("X-USER-EMAIL", "notfound@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("현재 비밀번호가 일치하지 않습니다."));
    }

    // 로그인 정보 조회 실패 - 존재하지 않는 이메일
    @Test
    @DisplayName("로그인 정보 조회 실패 - 존재하지 않는 이메일")
    void getLoginInfoByEmail_Fail_EmailNotFound() throws Exception {
        // given
        String nonExistingEmail = "notfound@test.com";
        when(memberService.getLoginInfoByEmail(nonExistingEmail))
                .thenThrow(new NotExistMemberException("회원을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/members/login-info/{email}", nonExistingEmail)
                        .header("X-USER-ROLE", "ROLE_ADMIN")
                        .header("X-USER-EMAIL", "notfound@test.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."));
    }

}
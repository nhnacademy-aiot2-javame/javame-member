package com.nhnacademy.exam.javamememberapi.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.exam.javamememberapi.member.controller.MemberController;
import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberRegisterRequest;
import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    ObjectMapper objectMapper;

    Role role;
    Member member;

    @BeforeEach
    void setUp() {
        role = new Role("ROLE_ADMIN", "ADMIN", "관리자");
        member = Member.ofNewMember(
                "javame",
                "password",
                "홍길동",
                LocalDate.of(2025, 4, 11),
                "javame@naver.com",
                "010-1234-5678",
                "M",
                role
        );
    }
    
}
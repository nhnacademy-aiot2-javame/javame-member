//
//package com.nhnacademy.exam.javamememberapi.member;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nhnacademy.exam.javamememberapi.member.controller.MemberController;
//import com.nhnacademy.exam.javamememberapi.member.domain.Member;
//import com.nhnacademy.exam.javamememberapi.member.dto.MemberRegisterRequest;
//import com.nhnacademy.exam.javamememberapi.member.dto.MemberResponse;
//import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
//import com.nhnacademy.exam.javamememberapi.role.domain.Role;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(controllers = MemberController.class)
//class MemberControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    MemberService memberService;
//
////    @MockitoBean
////    ObjectMapper objectMapper;
//
//    MemberResponse memberResponse;
//    @BeforeEach
//    void setUp() {
//        memberResponse = new MemberResponse(
//                1L,
//                "javame",
//                "홍길동",
//                LocalDate.of(2025,1,1),
//                "nhnacademy@naver.com",
//                "010-1111-1111",
//                "M",
//                "ROLE_ADMIN"
//        );
//    }
////어서와~~~ 테스트 처음이지?
//
//    @Test
//    @DisplayName("get")
//    void registerMemberTest() throws Exception {
//        Mockito.when(memberService.registerMember(Mockito.any())).thenReturn(memberResponse);
//        mockMvc
//                .perform(post("/api/v1/members")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(
//                                """
//{
//    "memberBirth": "2025-01-01",
//    "memberEmail": "nane@naver.com",
//    "memberId": "javame",
//    "memberMobile": "010-1234-5678",
//    "memberName": "홍길동",
//    "memberPassword": "password",
//    "memberSex": "M",
//    "roleId": "ROLE_ADMIN"
//}
//                                """
//                        ))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.memberId").value("javame"))
//                .andDo(print());
//    }
//}
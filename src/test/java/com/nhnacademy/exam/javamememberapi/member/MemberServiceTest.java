package com.nhnacademy.exam.javamememberapi.member;

import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberRegisterRequest;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberResponse;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberUpdateRequest;
import com.nhnacademy.exam.javamememberapi.member.repository.MemberRepository;
import com.nhnacademy.exam.javamememberapi.member.service.Impl.MemberServiceImpl;
import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import com.nhnacademy.exam.javamememberapi.role.repository.RoleRepository;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.time.LocalDate;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class MemberServiceTest {

    @Autowired
    TestEntityManager em;

    @Mock
    MemberRepository memberRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    MemberServiceImpl memberService;

    Role role;
    Member member;
    @BeforeEach
    void setUp(){
        LocalDate date = LocalDate.of(2025,4,11);
        role = new Role("ROLE_ADMIN", "ADMIN", "Description");

        member = Member.ofNewMember(
                "javame",
                "Qwer1234!@#$",
                "홍길동",
                date,
                "nhnacademy@naver.com",
                "010-1234-5678",
                "M",
                role
        );
    }

//    @Test
//    @DisplayName("Register Member Test")
//    void registerMemberTest(){
//        Role userRole = new Role("ROLE_USER", "USER", "This is User");
//        em.persistAndFlush(userRole);
//
//        LocalDate date = LocalDate.of(2025,4,11);
//        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
//                "javame",
//                "홍길동",
//                "Qwer1234!@#$",
//                "javame@naver.com",
//                date,
//                "010-1234-5678",
//                "M"
//        );
//
//        Mockito.when(memberRepository.existsMemberByMemberId(Mockito.anyString())).thenReturn(false);
//        Mockito.when(memberRepository.save(Mockito.any(Member.class))).thenReturn(member);
//
//        MemberResponse memberResponse = memberService.registerMember(memberRegisterRequest);
//        Assertions.assertNotNull(memberResponse);
//        Assertions.assertAll(
//                ()->Assertions.assertEquals("javame", memberResponse.getMemberId()),
//                ()->Assertions.assertEquals("홍길동", memberResponse.getMemberName()),
//                ()->Assertions.assertEquals("nhnacademy@naver.com", memberResponse.getMemberEmail()),
//                ()->Assertions.assertEquals("010-1234-5678", memberResponse.getMemberMobile()),
//                ()->Assertions.assertEquals("M", memberResponse.getMemberSex()),
//                ()->Assertions.assertEquals("ROLE_ADMIN", memberResponse.getRoleId())
//
//        );
//
//    }

    @Test
    @DisplayName("getMemberById Test")
    void getMemberByIdTest(){
        Mockito.when(memberRepository.getMemberByMemberId(Mockito.anyString())).thenReturn(Optional.of(member));
        MemberResponse memberResponse = memberService.getMemberByMemberId("javame");

        Assertions.assertNotNull(memberResponse);
        Assertions.assertAll(
                ()->Assertions.assertEquals("javame", memberResponse.getMemberId()),
                ()->Assertions.assertEquals("홍길동", memberResponse.getMemberName()),
                ()->Assertions.assertEquals("nhnacademy@naver.com", memberResponse.getMemberEmail()),
                ()->Assertions.assertEquals("010-1234-5678", memberResponse.getMemberMobile()),
                ()->Assertions.assertEquals("M", memberResponse.getMemberSex()),
                ()->Assertions.assertEquals("ROLE_ADMIN", memberResponse.getRoleId())
        );
    }

    @Test
    @DisplayName("getMemberByMemberNo Test")
    void getMemberByMemberNoTest(){
        Mockito.when(memberRepository.getMemberByMemberNo(Mockito.anyLong())).thenReturn(Optional.of(member));
        MemberResponse memberResponse = memberService.getMemberByMemberNo(1L);

        Assertions.assertNotNull(memberResponse);
        Assertions.assertAll(
                ()->Assertions.assertEquals("javame", memberResponse.getMemberId()),
                ()->Assertions.assertEquals("홍길동", memberResponse.getMemberName()),
                ()->Assertions.assertEquals("nhnacademy@naver.com", memberResponse.getMemberEmail()),
                ()->Assertions.assertEquals("010-1234-5678", memberResponse.getMemberMobile()),
                ()->Assertions.assertEquals("M", memberResponse.getMemberSex()),
                ()->Assertions.assertEquals("ROLE_ADMIN", memberResponse.getRoleId())
        );
    }

    @Test
    @DisplayName("updateMember Test")
    void updateMemberTest(){
        LocalDate date = LocalDate.of(2025,4,12);
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("changeId", "changePassword", "change@naver.com", date, "010-1234-5678");

        Mockito.when(memberRepository.getMemberByMemberId(Mockito.anyString())).thenReturn(Optional.of(member));

        MemberResponse memberResponse = memberService.updateMember("javame",memberUpdateRequest);

        Assertions.assertNotNull(memberResponse);
        Assertions.assertAll(
                ()->Assertions.assertEquals("javame", memberResponse.getMemberId()),
                ()->Assertions.assertEquals("change@naver.com", memberResponse.getMemberEmail()),
                ()->Assertions.assertEquals(date, memberResponse.getMemberBirth()),
                ()->Assertions.assertEquals("010-1234-5678", memberResponse.getMemberMobile())
        );
    }

    @Test
    @DisplayName("delete Member Test")
    void deleteMemberTest(){
        Mockito.when(memberRepository.existsMemberByMemberId(Mockito.anyString())).thenReturn(true);
        Mockito.when(memberRepository.getMemberByMemberId(Mockito.anyString())).thenReturn(Optional.of(member));
        memberService.deleteMember("javame");

        Mockito.verify(memberRepository, Mockito.times(1)).existsMemberByMemberId(Mockito.anyString());

    }
}

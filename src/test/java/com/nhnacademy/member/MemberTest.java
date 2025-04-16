package com.nhnacademy.member;

import com.nhnacademy.member.domain.Member;
import com.nhnacademy.role.domain.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;


@ActiveProfiles("test")
@DataJpaTest
public class MemberTest {

    @Autowired
    TestEntityManager entityManager;

    Member member;

    @BeforeEach
    void setUp(){
        LocalDate date = LocalDate.of(2025,4,11);
        Role role = new Role("ROLE_ADMIN", "ADMIN", "This is admin");
        entityManager.persist(role);
        member = Member.ofNewMember(
                "javame",
                "password",
                "홍길동",
                date,
                "nhnacademy@naver.com",
                "010-1234-5678",
                "M",
                role
        );
        entityManager.persist(member);
    }

    @Test
    @DisplayName("Create Member Test")
    void registerMemberTest(){
        LocalDate date = LocalDate.of(2025,4,11);
        Role role = new Role("ROLE_OWNER", "OWNER", "This is OWNER");
        entityManager.persist(role);
        Member savedMember = Member.ofNewMember(
                "javayou",
                "password",
                "홍길동",
                date,
                "nhnacademy1@naver.com",
                "010-1234-5679",
                "M",
                role
        );
        entityManager.persist(savedMember);

        Member findMember = entityManager.find(Member.class, savedMember.getMemberNo());
        Assertions.assertNotNull(findMember);
        Assertions.assertAll(
                ()->Assertions.assertEquals("javayou", findMember.getMemberId()),
                ()->Assertions.assertEquals("password", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("홍길동", findMember.getMemberName()),
                ()->Assertions.assertEquals("nhnacademy1@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-1234-5679", findMember.getMemberMobile()),
                ()->Assertions.assertEquals("M", findMember.getMemberSex()),
                ()->Assertions.assertEquals("ROLE_OWNER", findMember.getRole().getRoleId())
        );
    }

    @Test
    @DisplayName("Member find Test")
    void getMemberTest(){
        Member findMember = entityManager.find(Member.class, member.getMemberNo());
        Assertions.assertNotNull(findMember);
        Assertions.assertAll(
                ()->Assertions.assertEquals("javame", findMember.getMemberId()),
                ()->Assertions.assertEquals("password", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("홍길동", findMember.getMemberName()),
                ()->Assertions.assertEquals("nhnacademy@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-1234-5678", findMember.getMemberMobile()),
                ()->Assertions.assertEquals("M", findMember.getMemberSex()),
                ()->Assertions.assertEquals("ROLE_ADMIN", findMember.getRole().getRoleId())
        );
    }

    @Test
    @DisplayName("Member update Test")
    void memberUpdateTest(){
        LocalDate date = LocalDate.of(2025,4,12);
        Member updateMember = entityManager.find(Member.class, member.getMemberNo());
        updateMember.update("passwordchange","이름변경", date, "change@naver.com", "010-2222-2222");

        Member findMember = entityManager.find(Member.class, member.getMemberNo());
        Assertions.assertAll(
                ()->Assertions.assertEquals("passwordchange", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("이름변경", findMember.getMemberName()),
                ()->Assertions.assertEquals("change@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-2222-2222", findMember.getMemberMobile())
        );
    }

    @Test
    @DisplayName("delete Member Test")
    void deleteMemberTest(){
        Member findMember = entityManager.find(Member.class, member.getMemberNo());
        entityManager.remove(findMember);

        Member deleteFindMember = entityManager.find(Member.class, findMember.getMemberNo());
        Assertions.assertNull(deleteFindMember);
    }
}
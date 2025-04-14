package com.nhnacademy.exam.javamememberapi.member;

import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.repository.MemberRepository;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import com.nhnacademy.exam.javamememberapi.role.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    Role role;

    @BeforeEach
    void setUp(){
        //테스트 메서드가 실행 될 때마다 멤버를 5명 미리 등록합니다.
        LocalDate date = LocalDate.of(2025,4,11);
        role = new Role("ROLE_ADMIN", "ADMIN", "Description");
        roleRepository.save(role);

        for (int i=1; i<6; i++){
            String memberId = "member%s".formatted(i);
            String memberPassword = "password%s".formatted(i);
            String memberName= "memberName%s".formatted(i);
            String memberEmail="member%s@naver.com".formatted(i);
            String memberMobile="010-1111-%s".formatted(i*1111);
            String memberSex;

            if(i%2==0){
                memberSex= "F";
            } else{
                memberSex ="M";

            }
            Member member = Member.ofNewMember(memberId, memberPassword, memberName, date, memberEmail, memberMobile, memberSex,role);
            Member memberSaved = memberRepository.save(member);

        }
    }

    @Test
    @DisplayName("멤버 저장")
    void saveMember(){
        LocalDate date = LocalDate.of(2025,4,11);
        Member member = Member.ofNewMember(
                "javame",
                "Qwer1234!@#$",
                "홍길동",
                date,
                "nhnacademy@naver.com",
                "010-1234-5678",
                "M",
                role
        );
        memberRepository.save(member);

        Optional<Member> optionalMember = memberRepository.findByMemberId(member.getMemberId());
        Assertions.assertTrue(optionalMember.isPresent());

        Member findMember = optionalMember.get();
        Assertions.assertAll(
                ()->Assertions.assertTrue(Objects.nonNull(findMember.getMemberNo())),
                ()->Assertions.assertEquals("javame", findMember.getMemberId()),
                ()->Assertions.assertEquals("Qwer1234!@#$", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("nhnacademy@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-1234-5678", findMember.getMemberMobile()),
                ()->Assertions.assertEquals("M", findMember.getMemberSex())
        );

    }

    @Test
    @DisplayName("멤버 아이디로 멤버 존재여부 체크")
    void existsMemberByMemberId() {
        Optional<Member> optionalMember = memberRepository.findByMemberId("member1");
        Assertions.assertTrue(optionalMember.isPresent());

        Member findMember = optionalMember.get();
        Assertions.assertAll(
                ()->Assertions.assertTrue(Objects.nonNull(findMember.getMemberNo())),
                ()->Assertions.assertEquals("member1", findMember.getMemberId()),
                ()->Assertions.assertEquals("password1", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("member1@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-1111-1111", findMember.getMemberMobile()),
                ()->Assertions.assertEquals("M", findMember.getMemberSex())
        );
    }

    @Test
    @DisplayName("회원번호로 멤버 가져오기")
    void getMemberByMemberNo(){
        Optional<Member> optionalMember = memberRepository.getMemberByMemberId("member1");
        Assertions.assertTrue(optionalMember.isPresent());

        Member findMember = optionalMember.get();
        Assertions.assertAll(
                ()->Assertions.assertTrue(Objects.nonNull(findMember.getMemberNo())),
                ()->Assertions.assertEquals("member1", findMember.getMemberId()),
                ()->Assertions.assertEquals("password1", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("member1@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-1111-1111", findMember.getMemberMobile()),
                ()->Assertions.assertEquals("M", findMember.getMemberSex())
        );
    }

    @Test
    @DisplayName("멤버 업데이트(비밀번호 변경)")
    void updateMember(){
        LocalDate date = LocalDate.of(2025,4,12);
        Optional<Member> optionalMember = memberRepository.findByMemberId("member2");
        Assertions.assertTrue(optionalMember.isPresent());
        Member member = optionalMember.get();
        member.update("passwordchange","이름변경", date, "change@naver.com", "010-2222-2222");

        Optional<Member> findOptionalMember = memberRepository.findByMemberId("member2");
        Assertions.assertTrue(findOptionalMember.isPresent());
        Member findMember = findOptionalMember.get();
        Assertions.assertAll(
                ()->Assertions.assertEquals("passwordchange", findMember.getMemberPassword()),
                ()->Assertions.assertEquals("이름변경", findMember.getMemberName()),
                ()->Assertions.assertEquals("change@naver.com", findMember.getMemberEmail()),
                ()->Assertions.assertEquals("010-2222-2222", findMember.getMemberMobile())
        );
    }

    @Test
    @DisplayName("멤버 삭제")
    void deleteMember(){
        Optional<Member> optionalMember = memberRepository.findByMemberId("member3");
        Assertions.assertTrue(optionalMember.isPresent());

        memberRepository.delete(optionalMember.get());
        Optional<Member> findOptionalMember = memberRepository.findByMemberId("member3");
        Assertions.assertTrue(findOptionalMember.isEmpty());
    }
}
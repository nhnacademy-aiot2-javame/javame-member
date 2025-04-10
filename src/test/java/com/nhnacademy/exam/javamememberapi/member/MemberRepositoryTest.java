package com.nhnacademy.exam.javamememberapi.member;

import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    List<Long> memberNos = new ArrayList<>();

    @BeforeEach
    void setUp(){
        //테스트 메서드가 실행 될 때마다 멤버를 5명 미리 등록합니다.
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
            Member member = Member.ofNewMember(memberId, memberPassword, memberName, memberEmail, memberMobile, memberSex);
            Member membersaved = memberRepository.save(member);
            Long memberNo = membersaved.getMemberNo();
            memberNos.add(memberNo);
        }
    }



    @Test
    @DisplayName("멤버 아이디로 멤버 존재여부 체크")
    void existsMemberByMemberId() {
        Boolean isExist = memberRepository.existsMemberByMemberId("member2");
        Assertions.assertTrue(isExist);
    }

    @Test
    @DisplayName("멤버 아이디로 멤버 가져오기")
    void getMemberByMemberId(){
        Optional<Member> optionalMember = memberRepository.getMemberByMemberId("member2");
        Assertions.assertTrue(optionalMember.isPresent());
        Assertions.assertAll(
                ()-> Assertions.assertEquals(memberNos.get(1), optionalMember.get().getMemberNo()),
                ()-> Assertions.assertEquals("member2", optionalMember.get().getMemberId()),
                ()-> Assertions.assertEquals("password2", optionalMember.get().getMemberPassword()),
                ()-> Assertions.assertEquals("memberName2", optionalMember.get().getMemberName()),
                ()-> Assertions.assertEquals("member2@naver.com", optionalMember.get().getMemberEmail()),
                ()-> Assertions.assertEquals("010-1111-2222", optionalMember.get().getMemberMobile()),
                ()-> Assertions.assertEquals("F", optionalMember.get().getMemberSex())
        );


    }


    @Test
    @DisplayName("회원번호로 멤버 가져오기")
    void getMemberByMemberNo(){
        Optional<Member> optionalMember = memberRepository.getMemberByMemberNo(memberNos.get(1));
        Assertions.assertTrue(optionalMember.isPresent());
        Assertions.assertAll(
                ()-> Assertions.assertEquals(memberNos.get(1), optionalMember.get().getMemberNo()),
                () -> Assertions.assertEquals("member2", optionalMember.get().getMemberId()),
                ()-> Assertions.assertEquals("password2", optionalMember.get().getMemberPassword()),
                ()-> Assertions.assertEquals("memberName2", optionalMember.get().getMemberName()),
                ()-> Assertions.assertEquals("member2@naver.com", optionalMember.get().getMemberEmail()),
                ()-> Assertions.assertEquals("010-1111-2222", optionalMember.get().getMemberMobile()),
                ()-> Assertions.assertEquals("F", optionalMember.get().getMemberSex()
                ));
    }

    @Test
    @DisplayName("회원번호로 멤버 존재여부 확인")
    void existsMemberByMemberNo(){
        Boolean isExist = memberRepository.existsMemberByMemberNo(memberNos.get(3));
        Assertions.assertTrue(isExist);

    }

}

package com.nhnacademy.exam.javamememberapi.member.repository;

import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long > {

    Boolean existsMemberByMemberId(String memberId);

    Member getMemberByMemberId(String memberId);

    Member getMemberByMemberNo(Long memberNo);

    Boolean existsMemberByMemberNo(Long memberId);
}

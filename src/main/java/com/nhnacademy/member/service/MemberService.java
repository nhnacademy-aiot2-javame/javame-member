package com.nhnacademy.member.service;

import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.request.MemberUpdateRequest;
import com.nhnacademy.member.dto.response.LoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;

public interface MemberService {

    MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest);

    MemberResponse getMemberByMemberId(String memberId);

    MemberResponse getMemberByMemberNo(Long memberNo);

    MemberResponse updateMember(String memberId, MemberUpdateRequest memberUpdateRequest);

    public void deleteMember(String memberId);

}

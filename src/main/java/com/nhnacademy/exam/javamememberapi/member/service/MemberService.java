package com.nhnacademy.exam.javamememberapi.member.service;

import com.nhnacademy.exam.javamememberapi.member.dto.*;

import java.lang.reflect.Member;

public interface MemberService {

    //    create
    MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest);

    //    read
    MemberResponse getMemberByMemberId(String memberId);

    MemberResponse getMemberByMemberNo(Long memberNo);

    // update
    MemberResponse updateMember(String memberId, MemberUpdateRequest memberUpdateRequest);

    // delete
    public void deleteMember(String memberId);

    LoginResponse getLoginInfo(LoginRequest loginRequest);

    LoginResponse getLoginInfo(String memberId);
}

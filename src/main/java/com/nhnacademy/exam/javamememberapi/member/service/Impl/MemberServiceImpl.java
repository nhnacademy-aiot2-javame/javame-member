package com.nhnacademy.exam.javamememberapi.member.service.Impl;

import com.nhnacademy.exam.javamememberapi.member.common.AlreadyExistException;
import com.nhnacademy.exam.javamememberapi.member.common.NotExistMemberException;
import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.dto.*;
import com.nhnacademy.exam.javamememberapi.member.repository.MemberRepository;
import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest) {

        if(memberRepository.existsMemberByMemberId(memberRegisterRequest.getMemberId())){
            throw new AlreadyExistException("이미 존재하는 회원입니다.");
        }
        Role role = new Role("ROLE_ADMIN", "ADMIN", "어드민 입니다.");
        Member member = Member.ofNewMember(memberRegisterRequest.getMemberId(), memberRegisterRequest.getMemberPassword(), memberRegisterRequest.getMemberName(),
                memberRegisterRequest.getMemberEmail(), memberRegisterRequest.getMemberMobile(), memberRegisterRequest.getMemberSex(), role);
        Member saveMember = memberRepository.save(member);

        return memberResponseMapper(saveMember);
    }

    @Override
    public MemberResponse getMemberByMemberId(String memberId) {
        Optional<Member> memberOptional = memberRepository.getMemberByMemberId(memberId);
        if (memberOptional.isEmpty()){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Member member = memberOptional.get();
        return memberResponseMapper(member);
    }

    @Override
    public MemberResponse getMemberByMemberNo(Long memberNo) {
        Optional<Member> memberOptional = memberRepository.getMemberByMemberNo(memberNo);
        if (!memberOptional.isPresent()){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Member member = memberOptional.get();
        return memberResponseMapper(member);
    }



    @Override
    public MemberResponse updateMember(String memberId, MemberUpdateRequest memberUpdateRequest) {
        Optional<Member> memberOptional = memberRepository.getMemberByMemberId(memberId);
        if (memberOptional.isEmpty()){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Member member = memberOptional.get();
        member.update(member.getMemberPassword());
        return memberResponseMapper(member);
    }

    @Override
    public void deleteMember(Long memberNo) {
        if(!memberRepository.existsMemberByMemberNo(memberNo)){
        throw new NotExistMemberException("존재하지 않는 회원입니다.");
    }
    Optional<Member> deleteTarget = memberRepository.getMemberByMemberNo(memberNo);
        if (!deleteTarget.isPresent()){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Member member = deleteTarget.get();
    memberRepository.delete(member);
    }

    @Override
    public LoginResponse getLoginInfo(LoginRequest loginRequest) {
        Member member = memberRepository.getMemberByMemberId(loginRequest.getMemberId()).orElseThrow(()-> new NotExistMemberException("존재하지 않는 회원입니다."));
        return new LoginResponse(member.getMemberId(), member.getMemberPassword(), member.getRole().getRoleId());
    }

    @Override
    public LoginResponse getLoginInfo(String memberId){
        Optional<Member> memberOptional = memberRepository.getMemberByMemberId(memberId);
        if (memberOptional.isEmpty()){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Member member = memberOptional.get();
        return new LoginResponse(member.getMemberId(), member.getMemberPassword(), member.getRole().getRoleId());
    }

    private MemberResponse memberResponseMapper(Member member){
        return new MemberResponse(
                member.getMemberNo(),
                member.getMemberId(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.getMemberSex(),
                member.getRole().getRoleId()
        );
    }
}

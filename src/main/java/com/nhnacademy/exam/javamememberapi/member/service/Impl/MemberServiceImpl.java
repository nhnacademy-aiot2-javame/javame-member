package com.nhnacademy.exam.javamememberapi.member.service.Impl;

import com.nhnacademy.exam.javamememberapi.member.common.AlreadyExistException;
import com.nhnacademy.exam.javamememberapi.member.common.NotExistMemberException;
import com.nhnacademy.exam.javamememberapi.member.domain.Member;
import com.nhnacademy.exam.javamememberapi.member.dto.*;
import com.nhnacademy.exam.javamememberapi.member.repository.MemberRepository;
import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import com.nhnacademy.exam.javamememberapi.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    public MemberServiceImpl(MemberRepository memberRepository, RoleRepository roleRepository) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest) {

        boolean existMember =memberRepository.existsMemberByMemberId(memberRegisterRequest.getMemberId());
        if(existMember){
            throw new AlreadyExistException("이미 존재하는 회원입니다.");
        }
        Role defaultRole = roleRepository.getRoleByRoleId("ROLE_USER") // <<<--- "ADMIN" 대신 "ROLE_USER" 사용
                .orElseThrow(() -> new NotExistMemberException("기본 사용자 역할(ROLE_USER)을 찾을 수 없습니다.")); // 예외 메시지 명확화
        log.info(defaultRole.getRoleName());
        Member member = Member.ofNewMember(memberRegisterRequest.getMemberId(), memberRegisterRequest.getMemberPassword(), memberRegisterRequest.getMemberName(),
                memberRegisterRequest.getMemberBirth(),memberRegisterRequest.getMemberEmail(), memberRegisterRequest.getMemberMobile(), memberRegisterRequest.getMemberSex(),defaultRole);
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
        if (memberOptional.isEmpty()){
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
        member.update(
                memberUpdateRequest.getMemberPassword(),
                memberUpdateRequest.getMemberName(),
                memberUpdateRequest.getMemberBirth(),
                memberUpdateRequest.getMemberEmail(),
                memberUpdateRequest.getMemberMobile());
        return memberResponseMapper(member);
    }

    @Override
    public void deleteMember(String memberId) {
        if(!memberRepository.existsMemberByMemberId(memberId)){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Optional<Member> deleteTarget = memberRepository.getMemberByMemberId(memberId);
        if (deleteTarget.isEmpty()){
            throw new NotExistMemberException("존재하지 않는 회원입니다.");
        }
        Member member = deleteTarget.get();
        memberRepository.delete(member);
    }

    @Override
    public LoginResponse getLoginInfo(LoginRequest loginRequest) {
        Member member = memberRepository.getMemberByMemberId(loginRequest.getId()).orElseThrow(()-> new NotExistMemberException("존재하지 않는 회원입니다."));
        return new LoginResponse(member.getMemberId(), member.getMemberPassword(), member.getRole().getRoleId());
    }

    @Override
    public LoginResponse getLoginInfo(String memberId){
        Optional<Member> memberOptional = memberRepository.getMemberByMemberId(memberId);
        if (memberOptional.isEmpty()){
            // UsernameNotFoundException 에 더 적합한 메시지 또는 커스텀 예외 사용 고려
            throw new NotExistMemberException("존재하지 않는 회원 ID 입니다: " + memberId);
        }
        Member member = memberOptional.get();
        // 역할 정보가 null이 아닌지 확인하는 로직 추가 고려
        if (member.getRole() == null || member.getRole().getRoleId() == null) {
            throw new IllegalStateException("회원의 역할 정보가 유효하지 않습니다: " + memberId);
        }
        return new LoginResponse(member.getMemberId(), member.getMemberPassword(), member.getRole().getRoleId());
    }



    private MemberResponse memberResponseMapper(Member member){
        return new MemberResponse(
                member.getMemberNo(),
                member.getMemberId(),
                member.getMemberName(),
                member.getMemberBirth(),
                member.getMemberEmail(),
                member.getMemberMobile(),
                member.getMemberSex(),
                member.getRole().getRoleId()
        );
    }
}

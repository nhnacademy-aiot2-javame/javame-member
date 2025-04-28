package com.nhnacademy.member.service.impl;

import com.nhnacademy.company.common.NotExistCompanyException;
import com.nhnacademy.company.domian.Company;
import com.nhnacademy.company.repository.CompanyRepository;
import com.nhnacademy.member.common.AlreadyExistMemberException;
import com.nhnacademy.member.common.NotExistMemberException;
import com.nhnacademy.member.domain.Member;
import com.nhnacademy.member.dto.request.MemberPasswordChangeRequest;
import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.response.MemberLoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.repository.MemberRepository;
import com.nhnacademy.member.service.MemberService;
import com.nhnacademy.role.common.NotExistRoleException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * MemberService 인터페이스의 구현 클래스입니다.
 * 회원 관련 주요 비즈니스 로직을 담당하며, 데이터베이스 트랜잭션을 관리합니다.
 */
/**
 * {@link MemberService} 인터페이스의 구현 클래스입니다.
 * 회원 가입, 조회, 수정, 탈퇴, 로그인 정보 제공 등 회원 관련 핵심 비즈니스 로직을 수행합니다.
 * 데이터베이스 트랜잭션을 관리하며, {@link PasswordEncoder}를 사용하여 비밀번호를 안전하게 처리합니다.
 * 이 구현체는 회사 등록과 회원 가입 API가 분리된 구조를 가정합니다.
 *
 * @see com.nhnacademy.member.service.MemberService
 * @see com.nhnacademy.member.domain.Member
 * @see com.nhnacademy.member.repository.MemberRepository
 */
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
@Transactional // 클래스 레벨 기본 트랜잭션 (메서드별 재정의 가능)
@Slf4j // Lombok의 @Slf4j 어노테이션으로 Logger 자동 생성
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    // application.yml 등 설정 파일에서 기본 사용자 역할 ID 주입
    @Value("${app.security.default-role-id:ROLE_USER}")
    private String defaultUserRoleId;

    /**
     * {@inheritDoc}
     * 이 메서드는 사전에 등록된 기존 회사에 새로운 멤버를 등록합니다.
     * 회사를 새로 생성하는 기능은 포함하지 않으며, 요청된 회사 도메인이 존재하지 않으면 예외가 발생합니다.
     * 등록 시 기본 사용자 역할({@code defaultUserRoleId})이 할당되며, 비밀번호는 해싱되어 저장됩니다.
     * 만약 요청된 이메일이 이미 시스템에 존재하면 {@code AlreadyExistMemberException}이 발생합니다.
     * 필요한 회사 또는 기본 역할 정보가 시스템에 없으면 {@code NotExistCompanyException} 또는
     * {@code NotExistRoleException}이 발생할 수 있습니다.
     */
    @Override
    public MemberResponse registerMember(MemberRegisterRequest request) {
        log.debug("회원 등록 요청 처리 시작: 이메일 {}", request.getMemberEmail());

        // 이메일 중복 확인
        if (memberRepository.existsByMemberEmail(request.getMemberEmail())) {
            log.warn("회원 등록 실패: 이미 존재하는 이메일 {}", request.getMemberEmail());
            throw new AlreadyExistMemberException("이미 존재하는 이메일 입니다 : " + request.getMemberEmail());
        }

        // 소속 회사 조회 (반드시 존재해야 함)
        Company company = companyRepository.findById(request.getCompanyDomain())
                .orElseThrow(() -> {
                    log.warn("회원 등록 실패: 존재하지 않는 회사 도메인 {}", request.getCompanyDomain());
                    return new NotExistCompanyException("가입하려는 회사 도메인('"
                            + request.getCompanyDomain()
                            + "')을 찾을 수 없습니다. 회사 등록을 먼저 진행해주세요.");
                });
        log.info("회사 확인됨: '{}'. 신규 멤버 등록 진행.", company.getCompanyDomain());

        // 기본 사용자 역할 조회
        Role userRole = roleRepository.findById(defaultUserRoleId)
                .orElseThrow(() -> {
                    log.error("회원 등록 실패: 시스템 기본 역할 '{}' 없음.", defaultUserRoleId);
                    return new NotExistRoleException("시스템 기본 역할("
                            + defaultUserRoleId
                            + ") 을 찾을 수 없습니다.");
                });
        log.info("신규 멤버에게 역할 '{}' 할당 예정.", defaultUserRoleId);

        // Member 엔티티 생성 및 저장
        Member newMember = Member.ofNewMember(company, userRole, request.getMemberEmail(), request.getMemberPassword());
        Member savedMember = memberRepository.save(newMember);
        log.info("회원 등록 성공: 이메일 '{}', ID '{}'", savedMember.getMemberEmail(), savedMember.getMemberNo());

        // 응답 DTO 변환 후 반환
        return mapToMemberResponse(savedMember);
    }

    /**
     * {@inheritDoc}
     * {@code @Transactional(readOnly = true)}로 설정되어 읽기 전용으로 동작합니다.
     * ID로 회원을 찾지 못하면 {@code NotExistMemberException}이 발생합니다.
     * 탈퇴한 회원도 조회될 수 있으며, 필요시 추가적인 활성 상태 확인 로직을 적용할 수 있습니다.
     */
    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long memberNo) {
        log.debug("회원 정보 조회 요청: ID {}", memberNo);
        Member member = findMemberByIdOrThrow(memberNo);
        log.debug("회원 정보 조회 성공: ID {}", memberNo);
        return mapToMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberByEmail(String memberEmail) {
        log.debug("회원 정보 조회 요청: email {}", memberEmail);
        Member member = memberRepository.findByMemberEmail(memberEmail).orElseThrow(
                                                                            ()-> new NotExistMemberException(
                                                                                    String.format("%s로 회원을 찾지 못했습니다.", memberEmail)));
        log.debug("회원 정보 조회 성공: email {}", memberEmail);
        return mapToMemberResponse(member);
    }

    /**
     * {@inheritDoc}
     * 요청으로 받은 현재 비밀번호와 저장된 비밀번호를 {@link PasswordEncoder#matches(CharSequence, String)}로 비교하여 검증합니다.
     * 검증 실패 시 {@code IllegalArgumentException}이 발생합니다.
     * 검증 성공 시 새 비밀번호를 해싱하여 {@link Member#changePassword(String)}를 통해 엔티티 상태를 변경하며,
     * JPA 변경 감지(Dirty Checking)에 의해 데이터베이스에 반영됩니다.
     * 대상 회원을 찾지 못하면 {@code NotExistMemberException}이 발생합니다.
     */
    @Override
    public void changeMemberPassword(Long memberNo, MemberPasswordChangeRequest request) {
        log.debug("회원 비밀번호 변경 요청: ID {}", memberNo);
        Member member = findMemberByIdOrThrow(memberNo);

        // 현재 비밀번호 일치 확인
        if (!Objects.equals(request.getCurrentPassword(), member.getMemberPassword())) {
            log.warn("비밀번호 변경 실패: 현재 비밀번호 불일치. ID {}", memberNo);
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 해싱 및 엔티티 업데이트
        member.changePassword(request.getNewPassword());
        log.info("회원 비밀번호 변경 성공: ID {}", memberNo);
    }

    /**
     * {@inheritDoc}
     * 회원 데이터를 물리적으로 삭제하는 대신, {@link Member#withdraw()} 메서드를 호출하여
     * 탈퇴 상태로 변경하는 논리적 삭제(Soft Delete)를 수행합니다.
     * JPA 변경 감지(Dirty Checking)에 의해 데이터베이스에 반영됩니다.
     * 대상 회원을 찾지 못하면 {@code NotExistMemberException}이 발생합니다.
     */
    @Override
    public void deleteMember(Long memberNo) {
        log.debug("회원 탈퇴 요청: ID {}", memberNo);
        Member member = findMemberByIdOrThrow(memberNo);
        member.withdraw();
        log.info("회원 탈퇴 처리 완료: ID {}", memberNo);
    }

    /**
     * {@inheritDoc}
     * {@code @Transactional(readOnly = true)}로 설정되어 읽기 전용으로 동작합니다.
     * 다른 서비스(예: 인증 서버)에서 사용자의 비밀번호 검증 및 역할 확인을 위해 사용됩니다.
     * 반환되는 {@link MemberLoginResponse}에는 **해싱된 비밀번호 원문**이 포함됩니다.
     * 이메일로 회원을 찾지 못하거나 해당 회원이 탈퇴 상태({@link Member#isActive()}가 false)이면
     * {@code NotExistMemberException}이 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public MemberLoginResponse getLoginInfoByEmail(String email) {
        log.debug("로그인 정보 조회 요청: 이메일 {}", email);
        Member member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> {
                    log.warn("로그인 정보 조회 실패: 존재하지 않는 이메일 {}", email);
                    return new NotExistMemberException("해당 이메일의 회원을 찾을 수 없습니다: " + email);
                });

        // 활성(탈퇴하지 않은) 회원인지 확인
        if (!member.isActive()) {
            log.warn("로그인 정보 조회 실패: 탈퇴한 회원 {}", email);
            throw new NotExistMemberException("탈퇴 처리된 회원입니다: " + email);
        }

        log.debug("로그인 정보 조회 성공: 이메일 {}", email);
        // 로그인 정보 DTO 생성 및 반환
        return new MemberLoginResponse(
                member.getMemberNo(),
                member.getMemberEmail(),
                member.getMemberPassword(),
                member.getRole() != null ? member.getRole().getRoleId() : null // 역할 정보 포함
        );
    }

    /**
     * @param memberEmail 마지막 로그인 시간을 변경할 memberEmail
     */
    @Override
    public void updateLoginAt(String memberEmail) {
        if(memberRepository.existsByMemberEmail(memberEmail)) {
            throw new NotExistMemberException(String.format("%s 에 해당하는 멤버는 존재하지 않습니다.", memberEmail));
        }
        Member member = memberRepository.findByMemberEmail(memberEmail).orElseThrow(() -> new NotExistMemberException("DB에서 찾지 못했습니다. "));
        member.updateLastLoginTime();
    }

    /**
     * 주어진 ID로 {@link Member}를 조회하고, 존재하지 않으면 {@code NotExistMemberException}을 발생시키는 내부 헬퍼 메서드입니다.
     * 서비스 내 여러 메서드에서 회원 조회 및 예외 처리 로직의 중복을 방지합니다.
     *
     * @param memberNo 조회할 회원의 고유 ID
     * @return 조회된 {@link Member} 엔티티
     * @throws NotExistMemberException 해당 ID의 회원이 데이터베이스에 존재하지 않을 경우
     */
    private Member findMemberByIdOrThrow(Long memberNo) {
        return memberRepository.findById(memberNo)
                .orElseThrow(() -> {
                    log.warn("내부 조회 실패: 존재하지 않는 회원 ID {}", memberNo);
                    return new NotExistMemberException("회원을 찾을 수 없습니다: ID " + memberNo);
                });
    }

    /**
     * {@link Member} 엔티티 객체를 API 응답에 사용될 {@link MemberResponse} DTO 객체로 변환합니다.
     * 연관된 엔티티(Company, Role)의 필드(도메인, 역할 ID)를 포함시키며, 비밀번호와 같은 민감 정보는 제외합니다.
     * 연관 엔티티 접근 시 LAZY 로딩에 의한 추가 쿼리 발생 가능성에 유의해야 합니다.
     *
     * @param member 변환할 {@link Member} 엔티티 객체
     * @return 필수 회원 정보가 담긴 {@link MemberResponse} DTO 객체
     */
    private MemberResponse mapToMemberResponse(Member member) {
        // 연관 객체가 null일 경우를 대비하여 안전하게 필드 접근
        String companyDomain = (member.getCompany() != null) ? member.getCompany().getCompanyDomain() : null;
        String roleId = (member.getRole() != null) ? member.getRole().getRoleId() : null;

        return new MemberResponse(
                member.getMemberNo(),
                member.getMemberEmail(),
                companyDomain,
                roleId
        );
    }
}
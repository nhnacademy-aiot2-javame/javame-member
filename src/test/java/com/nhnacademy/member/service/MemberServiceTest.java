package com.nhnacademy.member.service;

import com.nhnacademy.common.util.AESUtil;
import com.nhnacademy.common.util.HashUtil;
import com.nhnacademy.common.util.PasswordUtil;
import com.nhnacademy.company.common.NotExistCompanyException;
import com.nhnacademy.company.domain.Company;
import com.nhnacademy.company.domain.CompanyIndex;
import com.nhnacademy.company.repository.CompanyIndexRepository;
import com.nhnacademy.company.repository.CompanyRepository;
import com.nhnacademy.member.common.AlreadyExistMemberException;
import com.nhnacademy.member.common.NotExistMemberException;
import com.nhnacademy.member.common.NotMatchesPasswordException;
import com.nhnacademy.member.domain.Member;
import com.nhnacademy.member.domain.MemberIndex;
import com.nhnacademy.member.dto.request.MemberPasswordChangeRequest;
import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.response.MemberLoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.repository.MemberIndexRepository;
import com.nhnacademy.member.repository.MemberRepository;
import com.nhnacademy.member.service.impl.MemberServiceImpl;
import com.nhnacademy.role.common.NotExistRoleException;
import com.nhnacademy.role.domain.Role;
import com.nhnacademy.role.repository.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberIndexRepository memberIndexRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyIndexRepository companyIndexRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Company company;
    private MemberRegisterRequest memberRegisterRequest;

    private Role roleUser;
    private final String defaultRoleId = "ROLE_USER";

    private Role roleOwner; // Owner 역할 객체 필드 추가
    private final String ownerRoleId = "ROLE_OWNER"; // Owner 역할 ID 정의

    @BeforeEach
    void setUp() {
        // 테스트용 기본 객체 생성
        company = Company.ofNewCompany("test-comp.com", "Test Company", "contact@test-comp.com", "010-1234-5678", "Test Address 123");

        memberRegisterRequest = new MemberRegisterRequest(
                "newbie@test.com",       // memberEmail
                "password123",         // memberPassword
                company.getCompanyDomain() // companyDomain
        );

        roleUser = new Role(defaultRoleId, "USER", "일반 사용자");
        roleOwner = new Role(ownerRoleId, "OWNER", "소유주");

        ReflectionTestUtils.setField(AESUtil.class, "algorithm", "AES");
        ReflectionTestUtils.setField(AESUtil.class, "secretKey", "B3db/0BCMBBUcafzUryeTA==");

        ReflectionTestUtils.setField(memberService, "defaultUserRoleId", defaultRoleId);
        ReflectionTestUtils.setField(memberService, "defaultOwnerRoleId", ownerRoleId);
    }

    @Test
    @DisplayName("회원 등록 성공")
    void registerMember_Success() {
        String email = memberRegisterRequest.getMemberEmail();
        String companyDomain = memberRegisterRequest.getCompanyDomain();
        String hashedEmail = HashUtil.sha256Hex(email); // 실제 사용되는 값
        String hashedCompanyDomain = HashUtil.sha256Hex(companyDomain); // 실제 사용되는 값
        String encryptedEmail = AESUtil.encrypt(email);

        CompanyIndex companyIndex = new CompanyIndex(hashedCompanyDomain, "domain", company.getCompanyDomain());

        when(memberIndexRepository.existsByIndexAndFieldName(hashedEmail, "email")).thenReturn(false);
        when(companyIndexRepository.findByIndex(hashedCompanyDomain)).thenReturn(Optional.of(companyIndex));
        when(companyRepository.findById(companyIndex.getFieldValue()))
                .thenReturn(Optional.of(company));

        when(roleRepository.findById(defaultRoleId))
                .thenReturn(Optional.of(roleUser));

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepository.save(memberCaptor.capture())).thenAnswer(invocation -> {
            Member memberBeingSaved = invocation.getArgument(0);
            ReflectionTestUtils.setField(memberBeingSaved, "memberNo", 1L);
            return memberBeingSaved;
        });
        MemberResponse memberResponse = memberService.registerMember(memberRegisterRequest);

        Assertions.assertNotNull(memberResponse);

        Member captured = memberCaptor.getValue();
        Assertions.assertEquals(company, captured.getCompany());
        Assertions.assertEquals(encryptedEmail, captured.getMemberEmail());
        Assertions.assertTrue(PasswordUtil.matches(memberRegisterRequest.getMemberPassword(), captured.getMemberPassword()));

        // 불필요한 호출 제거됨
        verify(memberIndexRepository, times(1)).existsByIndexAndFieldName(hashedEmail, "email");
        verify(companyIndexRepository, times(1)).findByIndex(hashedCompanyDomain);
        verify(companyRepository, times(1)).findById(company.getCompanyDomain());
        verify(roleRepository, times(1)).findById(defaultRoleId);
        verify(memberRepository, times(1)).save(any(Member.class));

    }

    @Test
    @DisplayName("회원 등록 실패 - 이미 존재하는 이메일")
    void registerMember_Failure_AlreadyExists() {
        when(memberIndexRepository.existsByIndexAndFieldName(Mockito.any(), Mockito.anyString())).thenReturn(true);

        assertThatThrownBy(() ->
                memberService.registerMember(memberRegisterRequest))
                .isInstanceOf(AlreadyExistMemberException.class)
                .hasMessageContaining("이미 존재하는 이메일 입니다");

        verify(companyRepository, never()).findById(anyString());
        verify(roleRepository, never()).findById(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 등록 실패 - 인덱스 테이블에서 찾지 못함. ")
    void registerMember_Failure_CompanyNotFound_1() {
        when(memberIndexRepository.existsByIndexAndFieldName(Mockito.any(), Mockito.anyString())).thenReturn(false);
        when(companyIndexRepository.findByIndex(Mockito.anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                memberService.registerMember(memberRegisterRequest))
                .isInstanceOf(NotExistCompanyException.class)
                .hasMessageContaining("존재하지 않는 회사 도메인입니다. ");

        verify(roleRepository, never()).findById(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 등록 실패 - 회사 도메인을 DB에서 찾을 수 없음. ")
    void registerMember_Failure_CompanyNotFound_2() {
        CompanyIndex companyIndex =  new CompanyIndex(HashUtil.sha256Hex(company.getCompanyDomain()), "domain", AESUtil.encrypt(company.getCompanyDomain()));
        when(memberIndexRepository.existsByIndexAndFieldName(Mockito.any(), Mockito.anyString())).thenReturn(false);
        when(companyIndexRepository.findByIndex(Mockito.anyString())).thenReturn(Optional.of(companyIndex));
        when(companyRepository.findById(companyIndex.getFieldValue())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                memberService.registerMember(memberRegisterRequest))
                .isInstanceOf(NotExistCompanyException.class)
                .hasMessageContaining("가입하려는 회사 도메인('")
                .hasMessageContaining("')을 찾을 수 없습니다. 회사 등록을 먼저 진행해주세요.");

        verify(roleRepository, never()).findById(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 등록 실패 - 기본 역할 없음")
    void registerMember_Failure_RoleNotFound() {
        CompanyIndex companyIndex =  new CompanyIndex(HashUtil.sha256Hex(company.getCompanyDomain()), "domain", AESUtil.encrypt(company.getCompanyDomain()));
        when(memberIndexRepository.existsByIndexAndFieldName(Mockito.any(), Mockito.anyString())).thenReturn(false);
        when(companyIndexRepository.findByIndex(Mockito.anyString())).thenReturn(Optional.of(companyIndex));
        when(companyRepository.findById(companyIndex.getFieldValue())).thenReturn(Optional.ofNullable(company));
        when(roleRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() ->
                memberService.registerMember(memberRegisterRequest))
                .isInstanceOf(NotExistRoleException.class)
                .hasMessageContaining("시스템 기본 역할(")
                .hasMessageContaining(") 을 찾을 수 없습니다.");

        verify(memberRepository, never()).save(any(Member.class));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("소유주 등록 성공")
    void registerOwner_Success() {
        String email = memberRegisterRequest.getMemberEmail();
        String companyDomain = memberRegisterRequest.getCompanyDomain();
        String hashedEmail = HashUtil.sha256Hex(email); // 실제 사용되는 값
        String hashedCompanyDomain = HashUtil.sha256Hex(companyDomain); // 실제 사용되는 값

        CompanyIndex companyIndex = new CompanyIndex(hashedCompanyDomain, "domain", company.getCompanyDomain());

        when(memberIndexRepository.existsByIndexAndFieldName(hashedEmail, "email")).thenReturn(false);
        when(companyIndexRepository.findByIndex(hashedCompanyDomain)).thenReturn(Optional.of(companyIndex));
        when(companyRepository.findById(companyIndex.getFieldValue()))
                .thenReturn(Optional.of(company));

        when(roleRepository.findById(ownerRoleId))
                .thenReturn(Optional.of(roleOwner));

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepository.save(memberCaptor.capture())).thenAnswer(invocation -> {
            Member memberBeingSaved = invocation.getArgument(0);
            ReflectionTestUtils.setField(memberBeingSaved, "memberNo", 2L);
            return memberBeingSaved;
        });

        MemberResponse response = memberService.registerOwner(memberRegisterRequest);

        Assertions.assertNotNull(response);
        Member captureMember = memberCaptor.getValue();

        assertThat(AESUtil.decrypt(captureMember.getMemberEmail())).isEqualTo(memberRegisterRequest.getMemberEmail());
        Assertions.assertTrue(PasswordUtil.matches(memberRegisterRequest.getMemberPassword(), captureMember.getMemberPassword()));
        assertThat(captureMember.getCompany()).isSameAs(company);

        assertThat(captureMember.getRole()).isSameAs(roleOwner);


        verify(companyRepository, times(1)).findById(memberRegisterRequest.getCompanyDomain());
        verify(roleRepository, times(1)).findById(ownerRoleId);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("ID로 회원 조회 성공")
    void getMemberById_Success() {
        // given - 테스트용 데이터 및 Mock 설정
        Long existingMemberId = 1L;
        // mapToMemberResponse 에서 company, role 정보도 사용하므로 설정 필요
        Member foundMember = Member.ofNewMember(company, roleUser, "found@test.com", "password");
        // 테스트를 위해 ID 강제 설정
        ReflectionTestUtils.setField(foundMember, "memberNo", existingMemberId);

        // memberRepository.findById 가 호출되면 Optional<Member> 반환하도록 설정
        when(memberRepository.findById(existingMemberId)).thenReturn(Optional.of(foundMember));

        // when - 서비스 메서드 호출
        MemberResponse response = memberService.getMemberById(existingMemberId);

        // then - 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.getMemberNo()).isEqualTo(existingMemberId);
        assertThat(response.getMemberEmail()).isEqualTo("found@test.com");
        assertThat(response.getCompanyDomain()).isEqualTo(company.getCompanyDomain());
        assertThat(response.getRoleId()).isEqualTo(roleUser.getRoleId());

        // Mock 호출 검증
        verify(memberRepository, times(1)).findById(existingMemberId);
    }

    @Test
    @DisplayName("ID로 회원 조회 실패 - 존재하지 않는 회원")
    void getMemberById_Fail_NotFound() {
        // given - 테스트용 데이터 및 Mock 설정
        Long nonExistingMemberId = 999L;

        // memberRepository.findById 가 호출되면 Optional.empty() 반환하도록 설정
        when(memberRepository.findById(nonExistingMemberId)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.getMemberById(nonExistingMemberId))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다: ID " + nonExistingMemberId);

        // Mock 호출 검증
        verify(memberRepository, times(1)).findById(nonExistingMemberId);
    }

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void getMemberByEmail_Success() {
        // given - 테스트용 데이터 및 Mock 설정
        String existingEmail = "found@test.com";
        Member foundMember = Member.ofNewMember(company, roleUser, existingEmail, "password");
        ReflectionTestUtils.setField(foundMember, "memberNo", 1L); // ID 설정 (MemberResponse 변환 시 필요)

        MemberIndex memberIndex = new MemberIndex(HashUtil.sha256Hex(existingEmail), "email", AESUtil.encrypt(existingEmail));
        when(memberIndexRepository.findByIndex(HashUtil.sha256Hex(existingEmail))).thenReturn(Optional.of(memberIndex));

        // memberRepository.findByMemberEmail 이 호출되면 Optional<Member> 반환하도록 설정
        when(memberRepository.findByMemberEmail(memberIndex.getFieldValue())).thenReturn(Optional.of(foundMember));

        // when - 서비스 메서드 호출
        MemberResponse response = memberService.getMemberByEmail(existingEmail);

        // then - 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.getMemberNo()).isEqualTo(1L);
        assertThat(response.getMemberEmail()).isEqualTo(existingEmail);
        assertThat(response.getCompanyDomain()).isEqualTo(company.getCompanyDomain());
        assertThat(response.getRoleId()).isEqualTo(roleUser.getRoleId());

        // Mock 호출 검증
        verify(memberRepository, times(1)).findByMemberEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("이메일로 회원 조회 실패 - 필드 값이 null일 때")
    void getMemberByEmail_Fail_isNull() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            memberService.getMemberByEmail(null);
        });
    }

    @Test
    @DisplayName("이메일로 회원 조회 실패 - Index 테이블에서 찾지 못했을 때")
    void getMemberByEmail_Fail_NotFound_in_index() {
        // given - 테스트용 데이터 및 Mock 설정
        String nonExistingEmail = "notfound@test.com";
        Member foundMember = Member.ofNewMember(company, roleUser, nonExistingEmail, "password");
        ReflectionTestUtils.setField(foundMember, "memberNo", 1L); // ID 설정 (MemberResponse 변환 시 필요)

        // memberRepository.findByMemberEmail 이 호출되면 Optional.empty() 반환하도록 설정
        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.getMemberByEmail(nonExistingEmail))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining(nonExistingEmail + " 에 해당하는 멤버는 존재하지 않습니다."); // 서비스 코드의 예외 메시지 확인

    }

    @Test
    @DisplayName("이메일로 회원 조회 실패 - 테이블에서 찾지 못했을 때")
    void getMemberByEmail_Fail_NotFound_in_table() {
        // given - 테스트용 데이터 및 Mock 설정
        String nonExistingEmail = "notfound@test.com";
        Member foundMember = Member.ofNewMember(company, roleUser, nonExistingEmail, "password");
        ReflectionTestUtils.setField(foundMember, "memberNo", 1L); // ID 설정 (MemberResponse 변환 시 필요)
        MemberIndex memberIndex = new MemberIndex(HashUtil.sha256Hex(nonExistingEmail),
                "email",
                AESUtil.encrypt(nonExistingEmail));

        // memberRepository.findByMemberEmail 이 호출되면 Optional.empty() 반환하도록 설정
        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.of(memberIndex));
        when(memberRepository.findByMemberEmail(memberIndex.getFieldValue())).thenReturn(Optional.empty());
        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.getMemberByEmail(nonExistingEmail))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining(nonExistingEmail + "로 저장된 회원이 없습니다."); // 서비스 코드의 예외 메시지 확인

    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changeMemberPassword_Success() {
        // given - 테스트 데이터 및 Mock 설정
        Long memberId = 1L;
        String cPassword = "password123"; // 현재 비밀번호 (평문)
        String currentPassword = PasswordUtil.encode(cPassword);
        String newPassword = "newPassword456";  // 새 비밀번호 (평문)

        MemberPasswordChangeRequest request = new MemberPasswordChangeRequest(
                cPassword,
                newPassword
        );

        Member existingMember = Member.ofNewMember(
                company,
                roleUser,
                "user@test.com",
                currentPassword //암호화 된 비밀번호
        );
        ReflectionTestUtils.setField(existingMember, "memberNo", memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        memberService.changeMemberPassword(memberId, request);

        verify(memberRepository, times(1)).findById(memberId);

        // when - 비밀번호 변경 메서드 호출
        Assertions.assertTrue(PasswordUtil.matches(newPassword, existingMember.getMemberPassword()));

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 존재하지 않는 회원")
    void changeMemberPassword_Fail_MemberNotFound() {
        // given - 테스트 데이터 및 Mock 설정
        Long nonExistingMemberId = 999L;
        MemberPasswordChangeRequest request = new MemberPasswordChangeRequest("current", "new");

        // findById가 호출되면 Optional.empty() 반환하도록 설정
        when(memberRepository.findById(nonExistingMemberId)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.changeMemberPassword(nonExistingMemberId, request))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다: ID " + nonExistingMemberId);

        // Mock 호출 검증 (findById만 호출됨)
        verify(memberRepository, times(1)).findById(nonExistingMemberId);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void changeMemberPassword_Fail_PasswordMismatch() {
        // given - 테스트 데이터 및 Mock 설정
        Long memberId = 1L;
        String cPW = "password123";      // 실제 멤버의 비밀번호
        String correctCurrentPassword = PasswordUtil.encode(cPW);
        String incorrectCurrentPassword = "wrongPassword"; // 요청으로 들어온 틀린 비밀번호
        String newPassword = "newPassword456";

        MemberPasswordChangeRequest request = new MemberPasswordChangeRequest(incorrectCurrentPassword, newPassword);

        // 실제 비밀번호를 가진 Member 객체 생성
        Member existingMember = Member.ofNewMember(company, roleUser, "user@test.com", correctCurrentPassword);
        ReflectionTestUtils.setField(existingMember, "memberNo", memberId);

        // findById가 호출되면 existingMember 반환하도록 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.changeMemberPassword(memberId, request))
                .isInstanceOf(NotMatchesPasswordException.class)
                .hasMessageContaining("비밀번호 변경 실패: 현재 비밀번호 불일치. ID: "+memberId);

        // then (추가 검증) - 비밀번호가 변경되지 않았는지 확인
        Assertions.assertTrue(PasswordUtil.matches(cPW, correctCurrentPassword));

        // Mock 호출 검증 (findById만 호출됨)
        verify(memberRepository, times(1)).findById(memberId);
    }


    @Test
    @DisplayName("회원 탈퇴 성공 - withdraw 호출 검증")
    void deleteMember_Success() {
        // given - 테스트용 데이터 및 Mock 설정
        Long memberIdToDelete = 1L;

        Member existingMember = Member.ofNewMember(company, roleUser, "user@test.com", "password");
        ReflectionTestUtils.setField(existingMember, "memberNo", memberIdToDelete);
        Member spiedMember = spy(existingMember); // Member 객체를 spy로 감싸 withdraw() 호출 검증

        // memberRepository.findById 가 호출되면 spiedMember 반환하도록 설정
        when(memberRepository.findById(memberIdToDelete)).thenReturn(Optional.of(spiedMember));

        // when - 서비스 메서드 호출
        memberService.deleteMember(memberIdToDelete);

        // then - 검증
        // 1. findById가 1번 호출되었는지 확인
        verify(memberRepository, times(1)).findById(memberIdToDelete);
        // 2. Member 객체(spiedMember)의 withdraw() 메서드가 1번 호출되었는지 확인
        verify(spiedMember, times(1)).withdraw();
        // 3. 물리적 삭제 (deleteById)는 호출되지 않았는지 확인
        verify(memberRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 회원")
    void deleteMember_Fail_MemberNotFound() {
        // given - 테스트용 데이터 및 Mock 설정
        Long nonExistingMemberId = 999L;

        // memberRepository.findById 가 호출되면 Optional.empty() 반환하도록 설정
        when(memberRepository.findById(nonExistingMemberId)).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.deleteMember(nonExistingMemberId))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining("회원을 찾을 수 없습니다: ID " + nonExistingMemberId);

        // Mock 호출 검증 (findById만 호출됨)
        verify(memberRepository, times(1)).findById(nonExistingMemberId);
        verify(memberRepository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("로그인 정보 조회 성공")
    void getLoginInfoByEmail_Success() {
        // given - 테스트 데이터 및 Mock 설정
        String existingEmail = AESUtil.encrypt("activeUser@test.com");
        String password = PasswordUtil.encode("password123!");
        Member activeMember = Member.ofNewMember(company, roleUser, existingEmail, password);
        ReflectionTestUtils.setField(activeMember, "memberNo", 1L);
        MemberIndex index = new MemberIndex(HashUtil.sha256Hex(existingEmail),"email", existingEmail);

        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.of(index));
        // memberRepository.findByMemberEmail 가 호출되면 activeMember 반환하도록 설정
        when(memberRepository.findByMemberEmail(index.getFieldValue())).thenReturn(Optional.of(activeMember));

        // when - 서비스 메서드 호출
        MemberLoginResponse response = memberService.getLoginInfoByEmail(existingEmail);

        // then - 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.getMemberNo()).isEqualTo(1L);
        assertThat(response.getMemberEmail()).isEqualTo(AESUtil.decrypt(existingEmail));
        Assertions.assertTrue(PasswordUtil.matches("password123!", response.getMemberPassword()));
        assertThat(response.getMemberPassword()).isEqualTo(password); // 해싱된 비밀번호 원문 반환 검증
        assertThat(response.getRoleId()).isEqualTo(roleUser.getRoleId());

        // Mock 호출 검증
        verify(memberRepository, times(1)).findByMemberEmail(existingEmail);
    }

    @Test
    @DisplayName("로그인 정보 조회 실패 - 존재하지 않는 이메일")
    void getLoginInfoByEmail_Fail_EmailNotFound() {
        // memberRepository.findByMemberEmail 가 호출되면 Optional.empty() 반환하도록 설정
        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.empty());
        // when & then - 예외 발생 검증
        Assertions.assertThrows(NotExistMemberException.class, ()->{
            memberService.getLoginInfoByEmail("nonExistingEmail");
        });
    }

    @Test
    @DisplayName("로그인 정보 조회 실패 - 탈퇴한 회원")
    void getLoginInfoByEmail_Fail_InactiveMember() {
        // given - 테스트 데이터 및 Mock 설정
        String inactiveEmail = "inactive_user@test.com";
        String encryptedEmail = AESUtil.encrypt(inactiveEmail);

        Member inactiveMember = Member.ofNewMember(
                company,
                roleUser,
                encryptedEmail,
                PasswordUtil.encode("password"));

        ReflectionTestUtils.setField(inactiveMember, "memberNo", 2L);

        // spy로 감싸서 isActive()만 false로 변경
        Member spiedInactiveMember = spy(inactiveMember);
        doReturn(false).when(spiedInactiveMember).isActive();

        MemberIndex index = new MemberIndex(HashUtil.sha256Hex(inactiveEmail), "email", encryptedEmail);

        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.of(index));
        when(memberRepository.findByMemberEmail(encryptedEmail)).thenReturn(Optional.of(spiedInactiveMember)); // ✅ fix

        // when & then
        assertThatThrownBy(() -> memberService.getLoginInfoByEmail(inactiveEmail))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining("탈퇴 처리된 회원입니다: " + inactiveEmail);

    }

// MemberServiceTest.java 파일에 아래 테스트 메서드를 추가하세요.

    @Test
    @DisplayName("마지막 로그인 시간 업데이트 성공")
    void updateLoginAt_Success() {
        // given - 테스트 데이터 및 Mock 설정
        String existingEmail = "user@test.com";
        Member existingMember = Member.ofNewMember(company, roleUser, existingEmail, "password");
        ReflectionTestUtils.setField(existingMember, "memberNo", 1L);
        MemberIndex memberIndex = new MemberIndex(HashUtil.sha256Hex(existingEmail),
                "email",
                AESUtil.encrypt(existingEmail));
        // Member 객체를 spy로 만들어 updateLastLoginTime() 호출을 감시
        Member spiedMember = spy(existingMember);

        // memberRepository.findByMemberEmail 가 호출되면 spiedMember 반환하도록 설정
        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.of(memberIndex));
        when(memberRepository.findByMemberEmail(memberIndex.getFieldValue())).thenReturn(Optional.of(spiedMember));

        // when - 서비스 메서드 호출
        memberService.updateLoginAt(existingEmail);

        // then - 검증
        // 1. findByMemberEmail이 1번 호출되었는지 확인
        verify(memberRepository, times(1)).findByMemberEmail(memberIndex.getFieldValue());
        // 2. Member 객체(spiedMember)의 updateLastLoginTime() 메서드가 1번 호출되었는지 확인
        verify(spiedMember, times(1)).updateLastLoginTime();
        // 3. (선택) memberRepository.save()가 호출되지 않았는지 확인 (변경 감지 사용 시)
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("마지막 로그인 시간 업데이트 실패 - 존재하지 않는 이메일")
    void updateLoginAt_Fail_EmailNotFound() {
        // given - 테스트 데이터 및 Mock 설정
        String nonExistingEmail = "notfound@test.com";

        // memberRepository.findByMemberEmail 가 호출되면 Optional.empty() 반환하도록 설정
        when(memberIndexRepository.findByIndex(Mockito.any())).thenReturn(Optional.empty());

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> memberService.updateLoginAt(nonExistingEmail))
                .isInstanceOf(NotExistMemberException.class)
                .hasMessageContaining(String.format("%s 에 해당하는 멤버는 존재하지 않습니다.", nonExistingEmail));

        // Mock 호출 검증
        verify(memberIndexRepository, times(1)).findByIndex(Mockito.any());
    }
}
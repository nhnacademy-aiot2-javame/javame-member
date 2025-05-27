package com.nhnacademy.member.repository;

import com.nhnacademy.company.domain.Company;
import com.nhnacademy.member.domain.Member;
import com.nhnacademy.role.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@Slf4j
@ActiveProfiles("test")
@DataJpaTest
/**
 * MemberRepositoryTest는 MemberRepository의 CRUD 기능을 테스트하는 클래스입니다.
 * @see MemberRepository
 */
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Company company;
    private Role roleUser;
    private Role roleAdmin;
    private Role roleOwner;
    private Member memberUser;
    private Member memberAdmin;
    private Member memberOwner;

    @BeforeEach
    void setUp() {
        // 1. 회사 생성
        company = Company.ofNewCompany(
                "javame.com",
                "javame",
                "companytest@javame.com",
                "010-1234-5678",
                "내외중앙로 55"
        );

        // 2. 역할 3개 생성 (USER, ADMIN, OWNER)
        roleUser = new Role("ROLE_USER", "USER", "기본 유저");
        roleAdmin = new Role("ROLE_ADMIN", "ADMIN", "관리자");
        roleOwner = new Role("ROLE_OWNER", "OWNER", "소유주");

        // 3. 멤버 3명 생성 (각각 다른 역할, 다른 이메일)
        memberUser = Member.ofNewMember(company, roleUser, "user@test.com", "password123");
        memberAdmin = Member.ofNewMember(company, roleAdmin, "admin@test.com", "password456");
        memberOwner = Member.ofNewMember(company, roleOwner, "owner@test.com", "password789");

        // Company, Role 를 persist
        testEntityManager.persist(company);
        testEntityManager.persist(roleUser);
        testEntityManager.persist(roleAdmin);
        testEntityManager.persist(roleOwner);

        // Member 를 persist
        testEntityManager.persist(memberUser);
        testEntityManager.persist(memberAdmin);
        testEntityManager.persist(memberOwner);

        // 변경사항을 DB에 반영
        testEntityManager.flush();
    }

    @Test
    @DisplayName("새로운 멤버 저장 성공 - ID 생성 확인")
    void save_NewMember_ShouldGenerateId() {
        Member newMember = Member.ofNewMember(
                company,
                roleUser,
                "new@test.com",
                "newpassword123"
        );

        Member savedMember = memberRepository.save(newMember);

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getMemberNo()).isNotNull();

        assertThat(savedMember.getMemberEmail()).isNotNull();
        assertThat(savedMember.getMemberEmail()).isEqualTo("new@test.com");
        log.info("새로 저장된 멤버 ID: {}", savedMember.getMemberNo());

    }

    @Test
    @DisplayName("새로운 멤버 저장 후 즉시 조회 성공")
    void save_NewMember_ThenFindById_ShouldReturnSameMember() {
        Member newMember = Member.ofNewMember(
                company,
                roleUser,
                "another@test.com",
                "anotherpassword123"
        );
        Member savedMember = memberRepository.save(newMember);
        Long savedMemberId = savedMember.getMemberNo();

        Optional<Member> foundMember = memberRepository.findById(savedMemberId);
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get()).isEqualTo(savedMember);
        assertThat(foundMember.get().getMemberEmail()).isEqualTo("another@test.com");
        log.info("저장된 멤버 ID: {}", savedMemberId);
        log.info("조회된 멤버 ID: {}", foundMember.get().getMemberNo());
    }

    @Test
    @DisplayName("중복 된 이메일로 멤버 저장 시 예외 발생")
    void save_MemberWithDuplicateEmail_ShouldThrowException(){
        Member duplicatedMember = Member.ofNewMember(
                company,
                roleUser,
                "user@test.com",
                "duplicatepassword123"
        );

        assertThatThrownBy(() -> {
            memberRepository.save(duplicatedMember);
            memberRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("멤버 업데이트(비밀번호 변경)")
    void updateMember(){
        Member foundMember = memberRepository.findById(memberUser.getMemberNo())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        String newPassword = "newpassword123";

        foundMember.changePassword(newPassword);

        memberRepository.saveAndFlush(foundMember);

        Member updatedMember = testEntityManager.find(Member.class, foundMember.getMemberNo());

        assertThat(updatedMember.getMemberPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("멤버 삭제")
    void deleteMember() {
        Member memberToDelete = memberAdmin;

        memberRepository.delete(memberToDelete);
        memberRepository.flush();

        Optional<Member> deletedMemberOpt = memberRepository.findByMemberEmail(memberToDelete.getMemberEmail());
        assertThat(deletedMemberOpt).isNotPresent();

        Optional<Member> deletedMemberByIdOpt = memberRepository.findById(memberToDelete.getMemberNo());
        assertThat(deletedMemberByIdOpt).isNotPresent();

    }

    @Test
    @DisplayName("멤버 이메일로 멤버 존재여부 체크")
    void existsByMemberEmail() {
        // given: setUp에서 저장된 이메일들 (memberUser, memberAdmin)

        // when & then: 존재하는 이메일과 존재하지 않는 이메일로 체크
        assertThat(memberRepository.existsByMemberEmail(memberUser.getMemberEmail())).isTrue();
        assertThat(memberRepository.existsByMemberEmail(memberAdmin.getMemberEmail())).isTrue();
        assertThat(memberRepository.existsByMemberEmail("nonexistent@test.com")).isFalse();
    }

    @Test
    @DisplayName("멤버 이메일로 멤버 가져오기")
    void findByMemberEmail() {
        // given: setUp에서 저장된 이메일들 (memberUser, memberAdmin, memberOwner)

        // when: 각 멤버의 이메일로 조회 시도
        Optional<Member> foundUserOpt = memberRepository.findByMemberEmail(memberUser.getMemberEmail());
        Optional<Member> foundAdminOpt = memberRepository.findByMemberEmail(memberAdmin.getMemberEmail());
        Optional<Member> foundOwnerOpt = memberRepository.findByMemberEmail(memberOwner.getMemberEmail());
        Optional<Member> notFoundOpt = memberRepository.findByMemberEmail("nonexistent@test.com");

        // then: 각 조회가 성공하고, 해당 멤버 정보가 맞는지 확인
        assertThat(foundUserOpt).isPresent();
        assertThat(foundUserOpt.get().getMemberEmail()).isEqualTo(memberUser.getMemberEmail());
        assertThat(foundUserOpt.get().getRole().getRoleId()).isEqualTo("ROLE_USER");

        assertThat(foundAdminOpt).isPresent();
        assertThat(foundAdminOpt.get().getMemberEmail()).isEqualTo(memberAdmin.getMemberEmail());
        assertThat(foundAdminOpt.get().getRole().getRoleId()).isEqualTo("ROLE_ADMIN");

        assertThat(foundOwnerOpt).isPresent();
        assertThat(foundOwnerOpt.get().getMemberEmail()).isEqualTo(memberOwner.getMemberEmail());
        assertThat(foundOwnerOpt.get().getRole().getRoleId()).isEqualTo("ROLE_OWNER");

        // 존재하지 않는 이메일 조회 시 비어있는 Optional 반환 확인
        assertThat(notFoundOpt).isNotPresent();
    }

    @Test
    @DisplayName("소속된 회사로 멤버 가져오기")
    void findByCompany() {

        // given: setUp에서 저장된 회사 (company)와 멤버들 (memberUser, memberAdmin, memberOwner)

        // when: 회사로 멤버 조회
        List<Member> membersInCompany = memberRepository.findByCompany(company);

        // then: 조회된 멤버들이 모두 회사에 속하는지 확인
        assertThat(membersInCompany).hasSize(3);
        assertThat(membersInCompany)
                .extracting(Member::getMemberEmail)
                .containsExactlyInAnyOrder(
                        memberUser.getMemberEmail(),
                        memberAdmin.getMemberEmail(),
                        memberOwner.getMemberEmail()
                );
    }

    @Test
    @DisplayName("소속된 회사와 역할로 멤버 가져오기")
    void findByCompanyAndRole() {
        // given: setUp에서 저장된 company, roleUser, roleAdmin, roleOwner 객체

        // when: 회사와 각 역할로 멤버 목록 조회
        List<Member> userMembers = memberRepository.findByCompanyAndRole(company, roleUser);
        List<Member> adminMembers = memberRepository.findByCompanyAndRole(company, roleAdmin);
        List<Member> ownerMembers = memberRepository.findByCompanyAndRole(company, roleOwner);
        List<Member> adminMembersInDifferentCompany = memberRepository.findByCompanyAndRole(
                Company.ofNewCompany(
                "Example.com",
                "Example",
                "example@example.com",
                "010-9999-9999",
                "Example Address"
                ), roleAdmin);

        // then: 각 역할별 조회 결과가 예상과 일치하는지 확인
        assertThat(userMembers).hasSize(1);
        assertThat(userMembers.get(0).getMemberEmail()).isEqualTo(memberUser.getMemberEmail());
        assertThat(userMembers.get(0).getRole()).isEqualTo(roleUser); // 역할 객체 비교

        assertThat(adminMembers).hasSize(1);
        assertThat(adminMembers.get(0).getMemberEmail()).isEqualTo(memberAdmin.getMemberEmail());
        assertThat(adminMembers.get(0).getRole()).isEqualTo(roleAdmin);

        assertThat(ownerMembers).hasSize(1);
        assertThat(ownerMembers.get(0).getMemberEmail()).isEqualTo(memberOwner.getMemberEmail());
        assertThat(ownerMembers.get(0).getRole()).isEqualTo(roleOwner);

         assertThat(adminMembersInDifferentCompany).isEmpty();
    }
}
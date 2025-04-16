package com.nhnacademy.member.domain;


import com.nhnacademy.company.domian.Company;
import com.nhnacademy.role.domain.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원 정보를 저장하는 엔티티 클래스입니다.
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    /**
     * 회원 고유 식별자 (UUID).
     */
    @Id
    @Column(name = "member_id", length = 36)
    @Comment("회원 ID (UUID)")
    private String memberId;

    /**
     * 회원이 속한 회사 정보.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_domain", nullable = false)
    private Company company;

    /**
     * 회원 역할 정보.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * 회원 이메일 주소.
     */
    @Column(name = "member_email", length = 100, nullable = false, unique = true)
    @Comment("회원 이메일")
    private String memberEmail;

    /**
     * 회원 비밀번호.
     */
    @Column(name = "member_password", length = 60, nullable = false) // 길이 60으로 변경
    @Comment("비밀번호 (BCrypt 해시)")
    private String memberPassword;

    /**
     * 회원 이름.
     */
    @Column(name = "member_name", length = 50, nullable = false)
    @Comment("회원 이름")
    private String memberName;

    /**
     * 회원 가입 일시.
     */
    @Column(name = "registered_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime registeredAt;

    /**
     * 마지막 로그인 일시.
     */
    @Column(name = "last_login_at", nullable = true)
    @Comment("마지막 로그인 일시")
    private LocalDateTime lastLoginAt;

    /**
     * 탈퇴 일시.
     */
    @Column(name = "withdrawal_at", nullable = true)
    @Comment("탈퇴일시")
    private LocalDateTime withdrawalAt;

    /**
     * Member 엔티티의 비공개 생성자입니다.
     * 정적 팩토리 메서드(createMember)를 통해 객체를 생성해야 합니다.
     *
     * @param memberId      회원 ID (UUID)
     * @param company       소속 회사
     * @param role          회원 역할
     * @param memberEmail   회원 이메일
     * @param memberPassword 해싱된 비밀번호
     * @param memberName    회원 이름
     */
    public Member(String memberId, Company company, Role role, String memberEmail, String memberPassword, String memberName) {
        this.memberId = memberId;
        this.company = company;
        this.role = role;
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
    }

    /**
     * 새로운 Member 엔티티를 생성하는 정적 팩토리 메서드입니다.
     * 회원 ID는 UUID로 자동 생성됩니다.
     *
     * @param company       소속 회사
     * @param role          회원 역할
     * @param memberEmail   회원 이메일
     * @param memberPassword 해싱된 비밀번호
     * @param memberName    회원 이름
     * @return 새로 생성된 Member 엔티티
     */
    public static Member ofNewMember(Company company, Role role, String memberEmail,
                                     String memberPassword, String memberName) { // memberId 파라미터 제거
        if (company == null || role == null || memberEmail == null
                || memberPassword == null || memberName == null) {
            throw new IllegalArgumentException("Member 생성에 필요한 인자가 null입니다.");
        }
        String memberUuid = UUID.randomUUID().toString();
        return new Member(memberUuid, company, role, memberEmail, memberPassword, memberName);
    }
    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", company=" + company +
                ", role=" + role +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberPassword='" + memberPassword + '\'' +
                ", memberName='" + memberName + '\'' +
                ", registeredAt=" + registeredAt +
                ", lastLoginAt=" + lastLoginAt +
                ", withdrawalAt=" + withdrawalAt +
                '}';
    }
}
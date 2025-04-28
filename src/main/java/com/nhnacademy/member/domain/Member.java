package com.nhnacademy.member.domain;

import com.nhnacademy.company.domian.Company;
import com.nhnacademy.role.domain.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString
public class Member {

    /**
     * 회원 고유 식별자 (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    @Comment("회원 고유 식별자")
    private Long memberNo;

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
    @Column(name = "member_password", length = 60, nullable = false)
    @Comment("비밀번호 (BCrypt 해시)")
    private String memberPassword;


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

    @PrePersist
    protected void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

    /**
     * Member 엔티티의 비공개 생성자입니다.
     * 정적 팩토리 메서드(createMember)를 통해 객체를 생성해야 합니다.
     *
     * @param company       소속 회사
     * @param role          회원 역할
     * @param memberEmail   회원 이메일
     * @param memberPassword 해싱된 비밀번호
     */
    public Member(Company company, Role role, String memberEmail, String memberPassword) {
        this.company = company;
        this.role = role;
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
    }

    /**
     * 새로운 Member 엔티티를 생성하는 정적 팩토리 메서드입니다.
     * 회원 ID는 UUID로 자동 생성됩니다.
     *
     * @param company       소속 회사
     * @param role          회원 역할
     * @param memberEmail   회원 이메일
     * @param memberPassword 해싱된 비밀번호
     * @return 새로 생성된 Member 엔티티
     */
    public static Member ofNewMember(Company company, Role role, String memberEmail,
                                     String memberPassword) {
        if (company == null || role == null || memberEmail == null
                || memberPassword == null) {
            throw new IllegalArgumentException("Member 생성에 필요한 인자가 null입니다.");
        }
        return new Member(company, role, memberEmail, memberPassword);
    }

    /**
     * 회원의 비밀번호를 변경합니다.
     *
     * @param encodedPassword 새로 해싱된 비밀번호
     */
    public void changePassword(String encodedPassword) {
        if (encodedPassword != null && encodedPassword.length() == 60) {
            this.memberPassword = encodedPassword;
        } else {
            // 예외 처리 또는 로깅
            throw new IllegalArgumentException("유효하지 않은 비밀번호 형식입니다.");
        }
    }

    /**
     * 회원의 역할을 변경합니다.
     *
     * @param newRole 변경할 새로운 역할
     */
    public void changeRole(Role newRole) {
        if (newRole != null) {
            this.role = newRole;
        }
    }

    /**
     * 마지막 로그인 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 회원을 탈퇴 상태로 변경합니다.
     */
    public void withdraw() {
        this.withdrawalAt = LocalDateTime.now();
    }

    /**
     * 회원이 현재 활성 상태인지 확인합니다.
     *
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return this.withdrawalAt == null;
    }
}


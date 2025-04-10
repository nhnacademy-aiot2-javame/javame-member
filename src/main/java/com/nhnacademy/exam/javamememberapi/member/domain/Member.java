package com.nhnacademy.exam.javamememberapi.member.domain;

import com.nhnacademy.exam.javamememberapi.role.domain.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no", nullable = false)
    private Long memberNo;

    @Column(name = "member_id", unique = true, length = 10, nullable = false)
    @Comment("회원아이디")
    private String memberId;

    @Column(name = "member_password", length = 20, nullable = false)
    @Comment("비밀번호")
    private String memberPassword;

    @Column(name = "member_name", length = 20, nullable = false)
    @Comment("회원이름")
    private String memberName;

    @Column(name = "member_birth", nullable = true)
    @Comment("생년월일")
    private String memberBirth;

    @Column(name = "member_email", length = 30, nullable = false)
    @Comment("이메일-비밀번호 찾기용")
    private String memberEmail;

    @Column(name = "member_mobile", unique = true, length = 13, nullable = false)
    @Comment("전화번호")
    private String memberMobile;

    @Column(name = "member_sex", length = 1, nullable = false)
    @Comment("성별")
    private String memberSex;

    @Column(name = "member_joinedat", nullable = false)
    @Comment("가입일자")
    private LocalDateTime memberJoinedAt;

    @Column(name = "member_updatedat", nullable = true)
    @Comment("수정일자")
    private LocalDateTime memberUpdatedAt;

    @Column(name = "member_withdrawalat", nullable = true)
    @Comment("탈퇴일자")
    private LocalDateTime memberWithdrawalAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private Member(String memberId, String memberPassword, String memberName,
                    String memberEmail,  String memberMobile, String memberSex, Role role){
        this.memberId = memberId;
        this.memberPassword = memberPassword;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberMobile = memberMobile;
        this.memberSex = memberSex;
        this.role = role;
    }

    protected Member(){}

    public static Member ofNewMember(String memberId, String memberPassword, String memberName, String memberEmail, String memberMobile, String memberSex, Role role){
        return new Member(memberId, memberPassword, memberName, memberEmail, memberMobile, memberSex, role);
    }



    @PrePersist
    public void prePersist() {
        this.memberJoinedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.memberUpdatedAt = LocalDateTime.now();
    }

    public void withdraw(){
        this.memberWithdrawalAt = LocalDateTime.now();
    }

    public void update(String memberPassword){
        this.memberPassword = memberPassword;
    }


    public Long getMemberNo() {
        return memberNo;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberBirth() {
        return memberBirth;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public String getMemberSex() {
        return memberSex;
    }

    public LocalDateTime getMemberJoinedAt() {
        return memberJoinedAt;
    }

    public LocalDateTime getMemberUpdatedAt() {
        return memberUpdatedAt;
    }

    public LocalDateTime getMemberWithdrawalAt() {
        return memberWithdrawalAt;
    }

    public Role getRole() {
        return role;
    }
}

package com.nhnacademy.company.domain;

import com.nhnacademy.common.util.AESUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Company {

    /**
     * 회사 고유 도메인 주소 (기본키).
     * 예: "javame.com"
     */
    @Id
    @Column(name = "company_domain", length = 50)
    @Comment("회사 도메인 (PK)")
    private String companyDomain;

    /**
     * 회사 이름.
     */
    @Column(name = "company_name", length = 100, nullable = false)
    @Comment("회사명")
    private String companyName;

    /**
     * 회사 대표 이메일 주소.
     */
    @Column(name = "company_email", length = 100)
    @Comment("회사 이메일")
    private String companyEmail;

    /**
     * 회사 대표 연락처.
     */
    @Column(name = "company_mobile", length = 20, nullable = false)
    @Comment("회사 연락처")
    private String companyMobile;

    /**
     * 회사 주소.
     */
    @Column(name = "company_address", length = 200, nullable = false)
    @Comment("회사 주소")
    private String companyAddress;

    /**
     * 회사 정보 등록 일시.
     */
    @Column(name = "registered_at", nullable = false, updatable = false)
    @Comment("등록일시")
    private LocalDateTime registeredAt;

    /**
     * 회사의 서비스 활성화 여부.
     * 기본값은 true (활성).
     */
    @Column(name = "is_active", nullable = false)
    @Comment("활성화 여부")
    private boolean active = true;

    /**
     * 회사 정보 생성자.
     *
     * @param companyDomain  회사 도메인
     * @param companyName    회사 이름
     * @param companyEmail   회사 이메일
     * @param companyMobile  회사 연락처
     * @param companyAddress 회사 주소
     */
    private Company(String companyDomain, String companyName, String companyEmail,
                   String companyMobile, String companyAddress) {

        if (companyDomain == null || companyDomain.isBlank()) {
            throw new IllegalArgumentException("회사 도메인은 필수입니다.");
        }
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("회사 이름은 필수입니다.");
        }
        if (companyMobile == null || companyMobile.isBlank()) {
            throw new IllegalArgumentException("회사 연락처는 필수입니다.");
        }
        if (companyAddress == null || companyAddress.isBlank()) {
            throw new IllegalArgumentException("회사 주소는 필수입니다.");
        }
        this.companyDomain = companyDomain;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyMobile = companyMobile;
        this.companyAddress = companyAddress;
    }

    /**
     * 새로운 Company 엔티티를 생성하는 정적 팩토리 메서드입니다.
     *
     * @param companyDomain  회사 도메인 (PK)
     * @param companyName    회사 이름
     * @param companyEmail   회사 이메일 (선택 사항, null 가능)
     * @param companyMobile  회사 연락처
     * @param companyAddress 회사 주소
     * @return 새로 생성된 Company 엔티티
     */
    public static Company ofNewCompany(String companyDomain, String companyName, String companyEmail,
                                        String companyMobile, String companyAddress) {
        return new Company(companyDomain, companyName, companyEmail, companyMobile, companyAddress);
    }

    /**
     * 회사의 상세 정보를 수정합니다.
     *
     * @param companyName    새로운 회사 이름
     * @param companyMobile  새로운 회사 연락처
     * @param companyAddress 새로운 회사 주소
     */
    public void updateDetails(String companyName,
                              String companyMobile, String companyAddress) {
        if (companyName != null && !companyName.isBlank()) {
            this.companyName = companyName;
        }
        if (companyMobile != null && !companyMobile.isBlank()) {
            this.companyMobile = companyMobile;
        }
        if (companyAddress != null && !companyAddress.isBlank()) {
            this.companyAddress = companyAddress;
        }
    }

    public void updateEmail(String companyEmail){
        this.companyEmail = companyEmail;
    }

    /**
     * 회사를 비활성 상태로 변경합니다.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 비활성 상태인 회사를 다시 활성 상태로 변경합니다.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * 엔티티가 저장되기 전에 호출되어 등록 일시를 설정합니다.
     */
    @PrePersist
    protected void onPrePersist() {
        this.registeredAt = LocalDateTime.now();
    }
}

package com.nhnacademy.member.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member_index")
@Getter
@NoArgsConstructor
@ToString
public class MemberIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제 members 테이블과 연관짓기 위해 memberNo 저장
    @Column(name = "member_no", nullable = false)
    private Long memberNo;

    @Column(name = "field_name", length = 20, nullable = false)
    private String fieldName;

    // 해시된 값 (SHA-256 등)
    @Column(name = "hash_value", length = 64, nullable = false)
    private String hashValue;

    public MemberIndex(Long memberNo, String fieldName, String hashValue) {
        this.memberNo = memberNo;
        this.fieldName = fieldName;
        this.hashValue = hashValue;
    }
}
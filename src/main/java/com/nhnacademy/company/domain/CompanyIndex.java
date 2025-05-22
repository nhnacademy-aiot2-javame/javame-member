package com.nhnacademy.company.domain;

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
@Table(name = "companies_index")
@Getter
@NoArgsConstructor
@ToString
public class CompanyIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_domain", length = 50, nullable = false)
    private String companyDomain; // FK로 company 테이블과 연결할 예정

    @Column(name = "field_name", length = 30, nullable = false)
    private String fieldName;

    @Column(name = "hash_value", length = 64, nullable = false)
    private String hashValue;

    public CompanyIndex(String companyDomain, String fieldName, String hashValue) {
        this.companyDomain = companyDomain;
        this.fieldName = fieldName;
        this.hashValue = hashValue;
    }
}

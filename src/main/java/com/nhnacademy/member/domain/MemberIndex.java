package com.nhnacademy.member.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "members_index")
@Getter
@NoArgsConstructor
@ToString
public class MemberIndex {

    /**
     * 인덱스 값
     * sha256(String domain)
     */
    @Id
    @Column(name = "index", length = 64)
    private String index;

    /**
     * 필드명
     * ex) "회사 도메인"
     */
    @Column(name = "field_name", length = 10)
    private String fieldName;


    /**
     * 인코딩 된 값
     * AESUtil.encode(String domain)
     */
    @Column(name = "field_value", length = 64)
    private String fieldValue;

    /**
     * @param index 인덱스 값
     * @param fieldName 필드명
     * @param fieldValue 필드값
     */
    public MemberIndex(String index, String fieldName, String fieldValue){
        this.index = index;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

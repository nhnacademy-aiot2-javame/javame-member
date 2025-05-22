package com.nhnacademy.member.repository;

import com.nhnacademy.member.domain.MemberIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberIndexRepository extends JpaRepository<MemberIndex, Long> {

    /**
     * @param hashValue 해쉬값
     * @return boolean
     */
    boolean existsByHashValue(String hashValue);

    /**
     * @param hashValue 해쉬값
     * @param fieldName 필드명
     * @return boolean
     */
    boolean existsByHashValueAndFieldName(String hashValue, String fieldName);

    /**
     * @param hashValue 해쉬값
     * @return MemberIndex Optional 값
     */
    Optional<MemberIndex> findByHashValue(String hashValue);
}

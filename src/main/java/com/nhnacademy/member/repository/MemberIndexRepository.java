package com.nhnacademy.member.repository;

import com.nhnacademy.member.domain.MemberIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberIndexRepository extends JpaRepository<MemberIndex, String> {

    /**
     * @param index 해쉬값
     * @return boolean
     */
    boolean existsByIndex(String index);

    /**
     * @param index 해쉬값
     * @param fieldName 필드명
     * @return boolean
     */
    boolean existsByIndexAndFieldName(String index, String fieldName);

    /**
     * @param index 해쉬값
     * @return MemberIndex Optional 값
     */
    Optional<MemberIndex> findByIndex(String index);
}

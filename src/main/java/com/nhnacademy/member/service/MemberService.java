package com.nhnacademy.member.service;

import com.nhnacademy.member.domain.Member;
import com.nhnacademy.member.dto.request.MemberPasswordChangeRequest;
import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.response.MemberLoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.common.exception.ResourceAlreadyExistsException;
import com.nhnacademy.common.exception.ResourceNotFoundException;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 * Controller 계층과 Repository 계층 사이의 중간 역할을 수행합니다.
 */
public interface MemberService {

    /**
     * 새로운 회원을 시스템에 등록합니다.
     * 요청된 비밀번호는 안전하게 해싱되어 저장됩니다.
     * 기본 역할(예: ROLE_USER)이 할당됩니다.
     *
     * @param request 회원 가입에 필요한 정보 (이메일, 비밀번호, 이름, 회사 도메인)
     * @return 등록 완료된 회원의 정보 (비밀번호 제외)
     * @throws ResourceAlreadyExistsException 이미 해당 이메일로 가입된 회원이 있을 경우
     * @throws ResourceNotFoundException      요청된 회사 도메인이나 시스템 기본 역할이 존재하지 않을 경우
     */
    MemberResponse registerMember(MemberRegisterRequest request);


    MemberResponse registerOwner(MemberRegisterRequest request);


    /**
     * 회원 ID를 사용하여 특정 회원의 상세 정보를 조회합니다.
     *
     * @param memberNo 조회할 회원의 고유 ID
     * @return 조회된 회원의 정보 (비밀번호 제외)
     * @throws ResourceNotFoundException 해당 ID를 가진 회원을 찾을 수 없을 경우
     */
    MemberResponse getMemberById(Long memberNo);


    /**
     * 회원 Email을 사용하여 특정 회원의 상세 정보를 조회합니다.
     *
     * @param memberEmail 조회할 회원의 이메일
     * @return 조회된 회원의 정보 (비밀번호 제외)
     * @throws ResourceNotFoundException 해당 ID를 가진 회원을 찾을 수 없을 경우
     */
    MemberResponse getMemberByEmail(String memberEmail);

    /**
     * 회원의 비밀번호를 변경합니다.
     * 변경 전 현재 비밀번호를 확인하는 과정이 포함됩니다.
     *
     * @param memberNo 비밀번호를 변경할 회원의 고유 ID
     * @param request  현재 비밀번호와 변경할 새 비밀번호가 담긴 요청 DTO
     * @throws ResourceNotFoundException 해당 ID를 가진 회원을 찾을 수 없을 경우
     * @throws IllegalArgumentException  현재 비밀번호가 일치하지 않거나 새 비밀번호 형식이 유효하지 않을 경우
     */
    void changeMemberPassword(Long memberNo, MemberPasswordChangeRequest request);

    /**
     * 회원을 시스템에서 탈퇴 처리합니다 (논리적 삭제/Soft Delete).
     * 실제 데이터 레코드는 삭제되지 않으며, 탈퇴 상태를 표시하는 필드가 업데이트됩니다.
     *
     * @param memberNo 탈퇴 처리할 회원의 고유 ID
     * @throws ResourceNotFoundException 해당 ID를 가진 회원을 찾을 수 없을 경우
     */
    void deleteMember(Long memberNo);

    /**
     * 주어진 이메일 주소에 해당하는 회원의 로그인 관련 핵심 정보를 조회합니다.
     * 주로 다른 인증 서비스(Auth API 등)에서 비밀번호 검증 및 역할 확인을 위해 호출됩니다.
     *
     * @param email 조회할 회원의 이메일 주소
     * @return 조회된 회원의 고유 ID, 이메일, 해시된 비밀번호, 역할 ID
     * @throws ResourceNotFoundException 해당 이메일 주소를 가진 회원을 찾을 수 없을 경우
     */
    MemberLoginResponse getLoginInfoByEmail(String email);


    /**
     * 회원이 로그인 된 후 최근 로그인 기록을 업데이트합니다.
     * @param memberEmail
     */
    void updateLoginAt(String memberEmail);
}

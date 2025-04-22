package com.nhnacademy.member.controller;


import com.nhnacademy.member.dto.request.MemberPasswordChangeRequest;
import com.nhnacademy.member.dto.request.MemberRegisterRequest;
import com.nhnacademy.member.dto.request.MemberUpdateRequest;
import com.nhnacademy.member.dto.response.MemberLoginResponse;
import com.nhnacademy.member.dto.response.MemberResponse;
import com.nhnacademy.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 회원(Member) 관련 HTTP 요청을 처리하는 REST 컨트롤러입니다.
 * 회원 가입, 조회, 수정, 탈퇴, 로그인 정보 제공 등의 API 엔드포인트를 제공합니다.
 * 모든 경로는 "/api/v1/members"를 기본으로 합니다.
 */
@RestController
@RequestMapping(value = "/api/v1/members", produces = MediaType.APPLICATION_JSON_VALUE)// 기본 경로 및 JSON 형태 응답 타입 설정
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 신규 회원을 등록합니다 (기존 회사에 User 역할로 가입).
     * 요청 본문에는 회원 정보(이메일, 비밀번호, 이름)와 소속될 회사 도메인이 포함되어야 합니다.
     * 성공 시 HTTP 상태 코드 201 (Created)과 생성된 회원 정보를 반환합니다.
     *
     * @param request 회원 가입 정보 DTO ({@link MemberRegisterRequest})
     * @return 생성된 회원 정보 ({@link MemberResponse})와 상태 코드 201
     */
    @PostMapping
    public ResponseEntity<MemberResponse> registerMember(@Validated @RequestBody MemberRegisterRequest request) {
        MemberResponse response = memberService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주어진 회원 ID(UUID)에 해당하는 회원 정보를 조회합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 회원 정보를 반환합니다.
     *
     * @param memberId 조회할 회원의 UUID (경로 변수)
     * @return 조회된 회원 정보 ({@link MemberResponse})와 상태 코드 200
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable String memberId) {
        MemberResponse response = memberService.getMemberById(memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 회원 ID에 해당하는 회원의 정보(현재는 이름만)를 수정합니다.
     * 성공 시 HTTP 상태 코드 200 (OK)과 수정된 회원 정보를 반환합니다.
     *
     * @param memberId 수정할 회원의 UUID (경로 변수)
     * @param request  수정할 정보 DTO ({@link MemberUpdateRequest})
     * @return 수정된 회원 정보 ({@link MemberResponse})와 상태 코드 200
     */
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable String memberId,
            @Validated @RequestBody MemberUpdateRequest request) {
        MemberResponse response = memberService.updateMember(memberId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 주어진 회원 ID에 해당하는 회원의 비밀번호를 변경합니다.
     * 요청 본문에는 현재 비밀번호와 새 비밀번호가 포함되어야 합니다.
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다. (본문 없음)
     *
     * @param memberId 비밀번호를 변경할 회원의 UUID (경로 변수)
     * @param request  비밀번호 변경 정보 DTO ({@link MemberPasswordChangeRequest})
     * @return 상태 코드 204 (No Content)
     */
    @PutMapping("/{memberId}/password")
    public ResponseEntity<Void> changeMemberPassword(
            @PathVariable String memberId,
            @Validated @RequestBody MemberPasswordChangeRequest request ) {
        memberService.changeMemberPassword(memberId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주어진 회원 ID에 해당하는 회원을 탈퇴 처리(논리적 삭제)합니다.
     * 성공 시 HTTP 상태 코드 204 (No Content)를 반환합니다. (본문 없음)
     *
     * @param memberId 탈퇴 처리할 회원의 UUID (경로 변수)
     * @return 상태 코드 204 (No Content)
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable String memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주어진 이메일 주소에 해당하는 회원의 로그인 관련 정보를 조회합니다.
     * 이 API는 주로 다른 내부 서비스(예: 인증 서버)에서 호출됩니다.
     * 성공 시 HTTP 상태 코드 200 (OK)와 로그인 정보를 반환합니다.
     *
     * @param email 조회할 회원의 이메일 주소 (경로 변수)
     * @return 로그인 관련 정보 ({@link MemberLoginResponse})와 상태 코드 200
     */
    @GetMapping("/login-info/{email}")
    public ResponseEntity<MemberLoginResponse> getLoginInfoByEmail(
            @PathVariable String email) {
        MemberLoginResponse response = memberService.getLoginInfoByEmail(email);
        return ResponseEntity.ok(response);
    }

}
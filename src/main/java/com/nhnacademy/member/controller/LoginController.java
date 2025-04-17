package com.nhnacademy.member.controller;

import com.nhnacademy.member.dto.response.LoginResponse;
import com.nhnacademy.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/login"})
public class LoginController {

    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Auth 서비스의 MemberAdaptor가 호출하는 엔드포인트.
     * 사용자 ID로 로그인에 필요한 정보를 조회하여 반환합니다.
     * @param memberId 조회할 사용자 ID
     * @return LoginResponse DTO를 포함한 ResponseEntity
     */
    @GetMapping("/{memberId}") // 경로 변수 이름 통일 권장
    public ResponseEntity<LoginResponse> getLoginInfo(@PathVariable("memberId") String memberId) {
        // MemberService의 getLoginInfo(String) 메서드 호출
        LoginResponse loginResponse = memberService.getLoginInfo(memberId);

        // 서비스에서 null을 반환하는 경우 (사용자 없음) 404 Not Found 반환
        // 서비스에서 예외를 던지는 경우, 전역 예외 처리기(@ControllerAdvice)에서 처리되거나 500 오류 발생 가능
        if (loginResponse == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(loginResponse);
    }


//    @GetMapping
//    public ResponseEntity<LoginResponse> getLogin(LoginRequest loginRequest){
//        LoginResponse loginResponse =memberService.getLoginInfo(loginRequest);
//        return ResponseEntity.ok(loginResponse);
//    }
//
//    @PostMapping
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
//        LoginResponse loginResponse = memberService.getLoginInfo(loginRequest);
//        return ResponseEntity.ok(loginResponse);
//    }
//
//    @GetMapping("/{member-id}")
//    public ResponseEntity<LoginResponse> getMember(@PathVariable("member-id")String memberId){
////        LoginResponse loginResponse =memberService.getMemberByMemberId(memberId);
////        return ResponseEntity.ok(loginResponse);
//        return  null;
//    }
}

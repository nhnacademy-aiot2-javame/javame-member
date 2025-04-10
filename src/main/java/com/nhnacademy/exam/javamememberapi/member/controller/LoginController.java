package com.nhnacademy.exam.javamememberapi.member.controller;

import com.nhnacademy.exam.javamememberapi.member.dto.LoginRequest;
import com.nhnacademy.exam.javamememberapi.member.dto.LoginResponse;
import com.nhnacademy.exam.javamememberapi.member.dto.MemberResponse;
import com.nhnacademy.exam.javamememberapi.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/api/v1/login"})
public class LoginController {

    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }


    @GetMapping
    public ResponseEntity<LoginResponse> getLogin(LoginRequest loginRequest){
        LoginResponse loginResponse =memberService.getLoginInfo(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<LoginResponse> getMember(@PathVariable("member-id")String memberId){
//        LoginResponse loginResponse =memberService.getMemberByMemberId(memberId);
//        return ResponseEntity.ok(loginResponse);
        return  null;
    }
}

package com.nhnacademy.exam.javamememberapi.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 10, max = 20,  message = "비밀번호는 10자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,}$\n",
            message = "비밀번호는 최소 10자리 이상, 영어 대소문자 + 특수문자 포함"
    )
    private final String memberPassword;


    public MemberUpdateRequest(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

}

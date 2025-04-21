package com.nhnacademy.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// 필요한 다른 import 문들...
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig { // 클래스 이름은 다를 수 있음

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈을 등록합니다.
     * 여기서는 BCrypt 알고리즘을 사용하는 BCryptPasswordEncoder를 사용합니다.
     *
     * @return PasswordEncoder 구현체 빈
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 객체를 생성하여 반환
        return new BCryptPasswordEncoder();
    }

}

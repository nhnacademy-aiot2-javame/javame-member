package com.nhnacademy.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * PasswordEncode 유틸.
 */
@Component
public class PasswordUtil {
    /**
     * Security.crypto.bcrypt를 사용한 BcryptPasswordEncoder.
     */
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 들어오는 순수 String 비밀번호 값을 BcryptPasswordEncoder로 인코딩하는 메소드입니다.
     * @param input 인코딩할 비밀번호
     * @return 인코딩 된 비밀번호 값.
     */
    public static String encode(String input) {
        return passwordEncoder.encode(input);
    }

    /**
     * 인코딩되지 않은 값을 인코딩되어 저장된 비밀번호와 비교하여 맞는지 확인하는 메소드입니다.
     * @param input 인코딩되지 않은 비밀번호 값.
     * @param encodePassword 인코딩 되어 DB에 저장된 비밀번호 값.
     * @return true, false를 반환합니다.
     */
    public static boolean matches(String input, String encodePassword) {
        return passwordEncoder.matches(input, encodePassword);
    }
}

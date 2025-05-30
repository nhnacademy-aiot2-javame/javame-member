package com.nhnacademy.common.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * sha256Hex 알고리즘을 사용하기 위한 유틸입니다.
 */
public class HashUtil {

    private HashUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param input 해쉬값으로 반환할 string 값.
     * @return 해쉬값으로 변환된 값.
     */
    public static String sha256Hex(String input) {
        return DigestUtils.sha256Hex(input);
    }
}

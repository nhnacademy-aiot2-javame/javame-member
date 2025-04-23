package com.nhnacademy.company.common;

public class NotFoundCompanyByEmailException extends RuntimeException {
    public NotFoundCompanyByEmailException(String email) {
        super(String.format("%s 에 해당하는 회사를 찾지 못했습니다.", email));
    }

}

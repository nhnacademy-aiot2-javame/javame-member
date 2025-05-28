package com.nhnacademy.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller에서 사용할 Custom Annotation 입니다.
 * 허용할 역할들을 아래와 같이 작성하면
 *  #@HasRole({""}) Aspect 에서 받아 처리합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasRole {
    String[] value(); // 허용할 역할들을 지정
}

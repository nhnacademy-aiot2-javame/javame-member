package com.nhnacademy.exam.javamememberapi.member;


import com.nhnacademy.exam.javamememberapi.member.controller.MemberController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@WebMvcTest(
        controllers = MemberControllerTest.class,
        excludeAutoConfiguration =
)
@Import(MemberController.class)
public class MemberControllerTest {

}

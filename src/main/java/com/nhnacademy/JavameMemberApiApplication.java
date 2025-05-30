package com.nhnacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nhnacademy")
public class JavameMemberApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavameMemberApiApplication.class, args);
    }

}

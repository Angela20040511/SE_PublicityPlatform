package com.se.publicityplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.se.publicityplatform.mapper")
public class PublicityPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublicityPlatformApplication.class, args);
    }
}

package com.mine.built.it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BuiltItApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuiltItApplication.class, args);
    }

}

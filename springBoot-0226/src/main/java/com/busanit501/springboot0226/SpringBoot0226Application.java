package com.busanit501.springboot0226;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBoot0226Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot0226Application.class, args);
    }

}

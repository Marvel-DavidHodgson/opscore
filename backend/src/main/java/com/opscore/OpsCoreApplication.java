package com.opscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OpsCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpsCoreApplication.class, args);
    }
}

package com.kseb.collabtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KsebBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KsebBackendApplication.class, args);
    }

}

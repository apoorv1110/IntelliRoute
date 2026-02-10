package com.intelliroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntelliRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliRouteApplication.class, args);
    }
}


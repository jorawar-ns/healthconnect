package com.healthconnect.era;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EraServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EraServiceApplication.class, args);
    }
}

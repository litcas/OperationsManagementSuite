package com.rengu.operationsoanagementsuite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OperationsoanagementsuiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationsoanagementsuiteApplication.class, args);
    }
}

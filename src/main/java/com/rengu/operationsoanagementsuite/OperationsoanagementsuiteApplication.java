package com.rengu.operationsoanagementsuite;

import com.rengu.operationsoanagementsuite.Thread.UDPHearBeatReceiveThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class OperationsoanagementsuiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationsoanagementsuiteApplication.class, args);
        // 启动UDP监听线程
        new UDPHearBeatReceiveThread().run();
    }
}

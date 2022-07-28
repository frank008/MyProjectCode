package com.dfdk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.dfdk.mapper")
public class ApplicationMain {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationMain.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}

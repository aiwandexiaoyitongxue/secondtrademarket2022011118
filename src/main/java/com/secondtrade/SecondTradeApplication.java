package com.secondtrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableTransactionManagement
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@MapperScan("com.secondtrade.dao")
public class SecondTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondTradeApplication.class, args);
    }
}
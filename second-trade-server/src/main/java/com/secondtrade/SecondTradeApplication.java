package com.secondtrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.secondtrade.mapper")
@ComponentScan(basePackages = {
    "com.secondtrade.webcontroller",
    "com.secondtrade.config",
    "com.secondtrade.service",
    "com.secondtrade.service.impl",
    "com.secondtrade.mapper",
    "com.secondtrade.entity",
    "com.secondtrade.util"
})
public class SecondTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondTradeApplication.class, args);
    }
}
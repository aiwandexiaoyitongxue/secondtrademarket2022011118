package com.secondtrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.secondtrade.dao")
public class SecondTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondTradeApplication.class, args);
    }
}
package com.santcool19.investment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InvestmentAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvestmentAnalyzerApplication.class, args);
    }
}


package com.careerforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CareerForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerForgeApplication.class, args);
        System.out.println("=======================================================================");
        System.out.println("🚀 [CareerForge AI] Intelligent Career Development Ecosystem Started!");
        System.out.println("🌐 Application URL : http://localhost:8085");
        System.out.println("📊 H2 Console      : http://localhost:8085/h2-console");
        System.out.println("=======================================================================");
    }
}

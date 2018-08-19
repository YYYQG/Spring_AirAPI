package com.xxx.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableScheduling
@ComponentScan(basePackages={"com.xxx"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
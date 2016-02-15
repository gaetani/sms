package com.vagas.app;

import com.vagas.app.configuration.AppConfig;
import org.springframework.boot.SpringApplication;


public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication main = new SpringApplication(AppConfig.class);
        main.run(args);
    }

}

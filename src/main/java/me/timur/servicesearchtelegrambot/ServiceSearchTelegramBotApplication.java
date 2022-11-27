package me.timur.servicesearchtelegrambot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@RequiredArgsConstructor
@SpringBootApplication
@EnableAsync
public class ServiceSearchTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSearchTelegramBotApplication.class, args);
    }
}

package me.timur.servicesearchtelegrambot.bot.provider.service;

import org.springframework.scheduling.annotation.Async;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

public interface RestRequester {
//    @Async
//    void sendMessage(String chatId, String message);

    @Async
    void sendDocument(String chatId, String message);
}

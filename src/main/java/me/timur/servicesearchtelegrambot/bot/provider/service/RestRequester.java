package me.timur.servicesearchtelegrambot.bot.provider.service;

import org.springframework.core.io.Resource;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

public interface RestRequester {

    void sendMessage(String chatId, String text);

    String getFilePath(String chatId, String fileId);

    String downloadFile(String filePath);

    void sendDocument(String chatId, Resource resource);
}

package me.timur.servicesearchtelegrambot.bot.provider.service;

import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

public interface RestRequester {

    void sendMessage(String chatId, String text);

    String getFilePath(String chatId, String fileId);

    String downloadFile(String filePath);

    byte[] downloadFile2(String filePath) throws IOException;

    void sendDocument(String chatId, Resource resource);

    void sendPhoto(String chatId, Resource resource);
}

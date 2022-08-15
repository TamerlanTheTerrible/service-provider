package me.timur.servicesearchtelegrambot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by Temurbek Ismoilov on 06/08/22.
 */

public interface ChatLogService {
    void log(Update update, String textToLog);
    void log(Update update);
    String getLastChatCommand(Update update);
}

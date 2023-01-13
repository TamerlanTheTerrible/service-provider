package me.timur.servicesearchtelegrambot.service;

import me.timur.servicesearchtelegrambot.bot.provider.enums.ChatLogType;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by Temurbek Ismoilov on 06/08/22.
 */

public interface ChatLogService {
    void log(Update update, Outcome outcome);
    String getLastChatOutcome(Update update, ChatLogType type);
}

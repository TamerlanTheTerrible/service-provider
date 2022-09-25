package me.timur.servicesearchtelegrambot.bot.provider;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 13/08/22.
 */

@Component
public interface UpdateMapper {
    List<BotApiMethod<Message>> map(Update update);
}
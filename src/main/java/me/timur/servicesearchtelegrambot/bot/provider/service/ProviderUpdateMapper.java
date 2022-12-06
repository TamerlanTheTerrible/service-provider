package me.timur.servicesearchtelegrambot.bot.provider.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 13/08/22.
 */

public interface ProviderUpdateMapper {
    List<SendMessage> map(Update update);
}

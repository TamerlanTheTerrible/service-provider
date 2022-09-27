package me.timur.servicesearchtelegrambot.bot.provider;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 23/08/22.
 */

public interface ProviderUpdateHandler {
    SendMessage start(Update update);

    SendMessage unknownCommand(Update update);

    SendMessage getCategories(Update update);

    SendMessage getServicesByCategoryName(Update update);

    SendMessage saveServiceIfServiceFoundOrSearchFurther(Update update);

    SendMessage searchService(Update update);

    SendMessage handleQuery(Update update);
}

package me.timur.servicesearchtelegrambot.bot.provider.service;

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

    SendMessage requestServiceName(Update update);

    SendMessage getServicesByCategoryName(Update update);

    SendMessage saveServiceIfServiceFoundOrSearchFurther(Update update);

    SendMessage searchService(Update update);

    List<SendMessage> handleQuery(Update update);

    List<SendMessage> acceptQuery(Update update);

    SendMessage denyQuery(Update update);

    SendMessage requestPhone(Update update);

    SendMessage editCompanyName(Update update);

    SendMessage saveCompanyName(Update update);

    SendMessage requestWebsite(Update update);

    SendMessage requestInstagram(Update update);

    SendMessage requestTelegram(Update update);

    SendMessage requestCertificate(Update update);

    SendMessage requestCompanyInfo(Update update);

    SendMessage providerInfo(Update update);
}

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

    SendMessage editCompanyAddress(Update update);

    SendMessage saveCompanyAddress(Update update);

    SendMessage editWebsite(Update update);

    SendMessage saveWebsite(Update update);

    SendMessage getServicesByCategoryName(Update update);

    SendMessage saveServiceIfServiceFoundOrSearchFurther(Update update);

    SendMessage searchService(Update update);

    List<SendMessage> handleQuery(Update update);

    List<SendMessage> acceptQuery(Update update);

    SendMessage denyQuery(Update update);

    SendMessage requestPhone(Update update);

    SendMessage editCompanyName(Update update);

    SendMessage saveCompanyName(Update update);

    SendMessage requestCompanyInfo(Update update);

    SendMessage requestRegion(Update update);

    SendMessage providerInfo(Update update);

    SendMessage editInstagram(Update update);

    SendMessage saveInstagram(Update update);

    SendMessage editTelegram(Update update);

    SendMessage saveTelegram(Update update);

    SendMessage editCertificate(Update update);

    SendMessage saveCertificate(Update update);

    SendMessage getMyServices(Update update);

    SendMessage editProviderService(Update update);

    SendMessage unsubscribeFromService(Update update);

    SendMessage subscribeToService(Update update);
}

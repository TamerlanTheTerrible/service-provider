package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.service.NotificationService;
import me.timur.servicesearchtelegrambot.bot.provider.service.RestRequester;
import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import org.springframework.stereotype.Component;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RestRequester restRequester;

    @Override
    public void sendNotification(String clientTgId, ServiceProviderDTO provider) {
        sendInformation(clientTgId, provider);
    }

    private void sendInformation(String clientTgId, ServiceProviderDTO provider) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(provider.getName() + " готов обработать ваш заказ\n");

        if (provider.getUser().getUsername() != null)
            stringBuilder.append("\nПользователь: @" + provider.getUser().getUsername());

        if (provider.getPhone() != null)
            stringBuilder.append("\nТелефон: " + provider.getPhone());

        if (provider.getCompanyName() != null)
            stringBuilder.append("\nФирма: " + provider.getCompanyName());

        if (provider.getCompanyAddress() != null)
            stringBuilder.append("\nАдрес: " + provider.getCompanyAddress());

        if (provider.getWebSite() != null)
            stringBuilder.append("\nСайт: " + provider.getWebSite());

        if (provider.getInstagram() != null)
            stringBuilder.append("\nИнстаграм: " + provider.getInstagram());

        if (provider.getTelegram() != null)
            stringBuilder.append("\nТелеграм: " + provider.getTelegram());

        if (provider.getDateCreated() != null)
            stringBuilder.append("\nО фирме: " + provider.getDateCreated());

        restRequester.sendMessage(clientTgId, stringBuilder.toString());
    }
}

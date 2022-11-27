package me.timur.servicesearchtelegrambot.bot.provider.service;

import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

public interface NotificationService {
    @Async
    void sendNotification(String clientTgId, ServiceProviderDTO provider);
}

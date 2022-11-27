package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.dto.TelegramResponseDto;
import me.timur.servicesearchtelegrambot.bot.provider.service.NotificationService;
import me.timur.servicesearchtelegrambot.bot.provider.service.RestRequester;
import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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
        if (provider.getCertificateTgFileId() != null) {
            sendCertificate(clientTgId, provider.getCertificateTgFileId());
        }
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

    private void sendCertificate(String clientTgId, String certificateTgFileId) {
        try {
            //get file path
            final String response = restRequester.getFilePath(clientTgId, certificateTgFileId);
             Map<String, String> resultMap = (Map<String, String>) new ObjectMapper()
                    .readValue(response, TelegramResponseDto.class)
                    .getResult();

             //download file and save file
            final String filePath = resultMap.get("file_path");
            String responseBody = restRequester.downloadFile(filePath);
            final byte[] bytes = responseBody.getBytes();
            Path path = Paths.get("./certificate." + FilenameUtils.getExtension(filePath));
            if (path.toFile().exists()) {
                Files.delete(path);
            }
            Files.createFile(path);
            Files.write(path, bytes);

            //send file
            restRequester.sendDocument(clientTgId, new UrlResource(path.toUri()));

        } catch (Exception e) {
            log.error("ERROR during certificate sending: " + e.getMessage(), e);
        }
    }

}

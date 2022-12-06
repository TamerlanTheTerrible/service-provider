package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.dto.TelegramResponseDto;
import me.timur.servicesearchtelegrambot.bot.provider.service.NotificationService;
import me.timur.servicesearchtelegrambot.bot.provider.service.RestRequester;
import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
            final String filePath = getFilePath(clientTgId, certificateTgFileId);
            //download file and save file
            Path path = downloadFile(filePath, clientTgId);
            //send file
            sendFile(path, filePath, clientTgId);
            // delete file after transaction
            Files.deleteIfExists(path);
        } catch (Exception e) {
            log.error("ERROR during certificate sending: " + e.getMessage(), e);
        }
    }

    private String getFilePath(String clientTgId, String certificateTgFileId) throws JsonProcessingException {
        final String response = restRequester.getFilePath(clientTgId, certificateTgFileId);
        Map<String, String> resultMap = (Map<String, String>) new ObjectMapper().readValue(response, TelegramResponseDto.class)
                .getResult();

        return resultMap.get("file_path");
    }

    private Path downloadFile(String filePath, String clientTgId) throws IOException {
        String responseBody = restRequester.downloadFile(filePath);
        final byte[] bytes = responseBody.getBytes();
        final String fileExtension = FilenameUtils.getExtension(filePath);
        final String separator = File.separator;

        Path path = Paths.get("." + separator + "certificate" + clientTgId + "." + fileExtension);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.write(path, bytes);

        return path;
    }

    private void sendFile(Path path, String tgFilePath, String clientTgId) throws IOException {
        List<String> photoFormats = new ArrayList<>();
        photoFormats.add("jpg");
        photoFormats.add("jpeg");
        photoFormats.add("png");
        if (photoFormats.contains(FilenameUtils.getExtension(tgFilePath))) {
            restRequester.sendPhoto(clientTgId, new UrlResource(path.toUri()));
        } else {
            restRequester.sendDocument(clientTgId, new UrlResource(path.toUri()));
        }
    }

}

package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.service.RestRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

@Slf4j
@Component
public class RequestSenderImpl implements RestRequester {
    private final static String BASE_URL = "https://api.telegram.org";
    @Value("${bot.token}")
    private String CURRENT_BOT_TOKEN_URL;
    @Value("${bot.search.token}")
    private String SEARCH_BOT_TOKEN_URL;

//    @Override
//    public void sendDocument(String chatId, String fileId) {
//        log.info("Sending file {} to {}", fileId, chatId);
//        try {
//            //get file info
//            RestTemplate restTemplate = new RestTemplate();
//            String getDocumentInfoUrl = BASE_URL + CURRENT_BOT_TOKEN_URL + "/getFile" + "?chat_id="+ chatId +"&file_id=" + fileId;
//            final ResponseEntity<TelegramResponseDto> getDocResponse = restTemplate.getForEntity(getDocumentInfoUrl, TelegramResponseDto.class);
//            Map<String, String> getDocResponseBody =  (Map<String, String>)getDocResponse.getBody().getResult();
//
//            //download file
//            final String filePath = getDocResponseBody.get("file_path");
//            String getDocumentUrl = BASE_URL + "/file" + CURRENT_BOT_TOKEN_URL + "/" + filePath;
//            final byte[] bytes = restTemplate.getForEntity(getDocumentUrl, String.class).getBody().getBytes();
//            Path path = Paths.get("./certificate." + FilenameUtils.getExtension(filePath));
//            if (path.toFile().exists()) {
//                Files.delete(path);
//            }
//            Files.createFile(path);
//            Files.write(path, bytes);
//
//            //send file
//            Resource resource = new UrlResource(path.toUri());
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("document", resource);
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//            String sendDocumentUrl = BASE_URL + SEARCH_BOT_TOKEN_URL + "/sendDocument?chat_id=3728614";
//            final ResponseEntity<String> response = restTemplate.postForEntity(sendDocumentUrl, requestEntity, String.class);
//
//            log.info("RESPONSE: " + response);
//        } catch (Exception e) {
//            log.error("Error occurred during rest request to " + chatId + " ERROR: " + e.getMessage());
//        }
//    }

    @Override
    public void sendMessage(String chatId, String text) {
        final UriComponents uriComponents = uriBuilder()
                .path("/bot" + SEARCH_BOT_TOKEN_URL)
                .path("/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", text)
                .build();

        get(uriComponents);
    }

    @Override
    public String getFilePath(String chatId, String fileId) {
        final UriComponents uriComponents = uriBuilder()
                .path("/bot" + CURRENT_BOT_TOKEN_URL)
                .path("/getFile")
                .queryParam("chat_id", chatId)
                .queryParam("file_id", fileId)
                .build();

        return get(uriComponents);
    }

    @Override
    public String downloadFile(String filePath) {
        final UriComponents uriComponents = uriBuilder()
                .path("/file")
                .path("/bot" + CURRENT_BOT_TOKEN_URL)
                .path("/" + filePath)
                .build();

        return get(uriComponents);
    }

    @Override
    public byte[] downloadFile2(String filePath) {
//        return new URL("https://api.telegram.org/file/bot"+CURRENT_BOT_TOKEN_URL+"/"+filePath).openStream();
        final UriComponents uriComponents = uriBuilder()
                .path("/file")
                .path("/bot" + CURRENT_BOT_TOKEN_URL)
                .path("/" + filePath)
                .build();

        return new RestTemplate()
                .getForEntity(uriComponents.toUriString(), byte[].class)
                .getBody();
    }

    @Override
    public void sendDocument(String chatId, Resource resource) {
        final UriComponents uriComponents = uriBuilder()
                .path("/bot" + SEARCH_BOT_TOKEN_URL)
                .path("/sendDocument")
                .queryParam("chat_id", chatId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("document", resource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        post(uriComponents, requestEntity);
    }

    @Override
    public void sendPhoto(String chatId, Resource resource) {
        final UriComponents uriComponents = uriBuilder()
                .path("/bot" + SEARCH_BOT_TOKEN_URL)
                .path("/sendPhoto")
                .queryParam("chat_id", chatId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("photo", resource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        post(uriComponents, requestEntity);
    }

    private String get(UriComponents uriComponents) {
        log.info("REQUEST: {}", uriComponents);
        final ResponseEntity<String> response = new RestTemplate().getForEntity(uriComponents.toUriString(), String.class);
        if (!uriComponents.getPath().contains("/file")) {
            log.info("RESPONSE: {}", response);
        }
        return response.getBody();
    }

    private String post(UriComponents uriComponents, HttpEntity requestEntity) {
        log.info("REQUEST: {}", uriComponents);
        final ResponseEntity<String> response = new RestTemplate().postForEntity(uriComponents.toUriString(),requestEntity, String.class);
        log.info("RESPONSE: {}", response);
        return response.getBody();
    }

    private UriComponentsBuilder uriBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.telegram.org");
    }
}

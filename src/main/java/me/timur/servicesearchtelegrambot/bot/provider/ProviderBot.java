package me.timur.servicesearchtelegrambot.bot.provider;

import lombok.RequiredArgsConstructor;
import me.timur.servicesearchtelegrambot.bot.provider.service.ProviderUpdateMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 13/08/22.
 */

@Component
@RequiredArgsConstructor
public class ProviderBot extends TelegramLongPollingBot {

    private final ProviderUpdateMapper providerUpdateMapper;

    @Value("${bot.username}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handle(update);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handle(Update update) throws TelegramApiException {
        final List<SendMessage> sendMessageList = providerUpdateMapper.map(update);
        for (BotApiMethod<Message> message: sendMessageList) {
            execute(message);
        }
    }

}

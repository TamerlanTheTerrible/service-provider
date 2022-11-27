package me.timur.servicesearchtelegrambot.bot.provider;

import lombok.RequiredArgsConstructor;
import me.timur.servicesearchtelegrambot.bot.provider.service.ProviderUpdateMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
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

    private final static String BOT_NAME = "UzserviceproviderBot";
    private final static String BOT_TOKEN = "5619769900:AAGHABIkbQ7DkItKLowv6N4cm_uW3rN4M1U";

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
//            execute(new GetFile("BQACAgIAAxkBAAO3Y3pMXymjuZ0Xa-0E0PE-53bc1D8AAnEeAALhUthLww8rKqczdeArBA"));
            handle(update);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handle(Update update) throws TelegramApiException {
        final List<BotApiMethod<Message>> sendMessageList = providerUpdateMapper.map(update);
        for (BotApiMethod<Message> message: sendMessageList) {
            execute(message);
        }
    }

}

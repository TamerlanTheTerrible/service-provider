package me.timur.servicesearchtelegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import me.timur.servicesearchtelegrambot.enitity.ChatLog;
import me.timur.servicesearchtelegrambot.repository.ChatLogRepository;
import me.timur.servicesearchtelegrambot.service.ChatLogService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static me.timur.servicesearchtelegrambot.bot.util.UpdateUtil.*;

/**
 * Created by Temurbek Ismoilov on 06/08/22.
 */

@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {

    private final ChatLogRepository chatLogRepository;

    @Override
    public void log(Update update, Outcome outcome) {
        final ChatLog chatLog = new ChatLog(update, outcome);
        chatLogRepository.save(chatLog);
    }

    @Override
    public String getLastChatOutcome(Update update) {
        final Optional<ChatLog> chatLogOpt = chatLogRepository.findTopByTgChatIdOrderByIdDesc(chatId(update));
        return chatLogOpt.map(chatLog -> chatLog.getOutcome().name()).orElse(null);
    }
}

package me.timur.servicesearchtelegrambot.bot.provider.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.ProviderUpdateHandler;
import me.timur.servicesearchtelegrambot.bot.provider.ProviderUpdateMapper;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Command;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import me.timur.servicesearchtelegrambot.service.ChatLogService;
import me.timur.servicesearchtelegrambot.service.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.timur.servicesearchtelegrambot.bot.util.UpdateUtil.command;

/**
 * Created by Temurbek Ismoilov on 25/09/22.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderUpdateMapperImpl implements ProviderUpdateMapper {

    private final ProviderUpdateHandler updateHandler;
    private final ChatLogService chatLogService;
    private final ServiceManager serviceManager;

    @Value("${channel.service.searcher.id.dev}")
    private Long serviceSearChannelId;

    @Override
    public List<BotApiMethod<Message>> map(Update update) {
        return tryToMap(update);
    }

    private List<BotApiMethod<Message>> tryToMap(Update update) {
        final List<String> serviceNames = serviceManager.getActiveServiceNames();
        final List<BotApiMethod<Message>> replyList = new ArrayList<>();

        SendMessage sendMessage = null;

        try {
            final String newCommand = command(update);
            final String lastChatCommand = chatLogService.getLastChatOutcome(update);
            //check if it is from the group
            if (update.getChannelPost() != null && Objects.equals(update.getChannelPost().getChatId(), serviceSearChannelId))
                replyList.addAll(updateHandler.handleQuery(update));
            // start command called
            else if (Objects.equals(newCommand, Command.START.getText()))
                sendMessage = updateHandler.start(update);
            // accept query
            else if (newCommand.contains(Command.ACCEPT_QUERY.getText()))
                sendMessage = updateHandler.acceptQuery(update);
            // deny query
            else if (newCommand.equals(Command.DENY_QUERY.getText()))
                sendMessage = updateHandler.denyQuery(update);
            // list of all services required
            else if (Objects.equals(newCommand, Outcome.CATEGORIES.getText()) || Objects.equals(newCommand, Command.BACK_TO_CATEGORIES.getText()) ) {
                sendMessage = updateHandler.getCategories(update);
            }
            // list of all services required
            else if (lastChatCommand.equals(Outcome.CATEGORIES.name()) || lastChatCommand.equals(Command.BACK_TO_CATEGORIES.name())) {
                sendMessage = updateHandler.getServicesByCategoryName(update);
            }
            // required service found
            else if ((lastChatCommand.equals(Outcome.SERVICES.name()) || lastChatCommand.equals(Outcome.SERVICE_SEARCH_FOUND.name())) && serviceNames.contains(newCommand)) {
                sendMessage = updateHandler.saveServiceIfServiceFoundOrSearchFurther(update);
            }
            // searching a service
            else if (lastChatCommand.equals(Outcome.START.name()) || lastChatCommand.equals(Outcome.SERVICE_SEARCH_NOT_FOUND.name()) || lastChatCommand.equals(Outcome.SERVICE_SEARCH_FOUND.name()))
                sendMessage = updateHandler.searchService(update);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage = updateHandler.unknownCommand(update);
        }

        if (sendMessage != null)
            replyList.add(sendMessage);

        return replyList;
    }
}

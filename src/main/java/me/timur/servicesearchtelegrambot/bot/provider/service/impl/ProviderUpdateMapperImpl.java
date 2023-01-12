package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Command;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import me.timur.servicesearchtelegrambot.bot.provider.service.ProviderUpdateHandler;
import me.timur.servicesearchtelegrambot.bot.provider.service.ProviderUpdateMapper;
import me.timur.servicesearchtelegrambot.service.ChatLogService;
import me.timur.servicesearchtelegrambot.service.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    @Value("${channel.service.searcher.id}")
    private Long serviceSearChannelId;

    @Override
    public List<SendMessage> map(Update update) {
        return tryToMap(update);
    }

    private List<SendMessage> tryToMap(Update update) {
        final List<String> serviceNames = serviceManager.getActiveServiceNames();
        final List<String> companyInfoCommands = getCompanyInfoCommandTexts();
        final List<SendMessage> replyList = new ArrayList<>();

        SendMessage sendMessage = null;

        try {
            final String newCommand = command(update) != null ? command(update) : "";
            final String lastChatCommand = chatLogService.getLastChatOutcome(update) != null ? chatLogService.getLastChatOutcome(update) : "";
            // start command called
            if (Objects.equals(newCommand, Command.START.getText()))
                sendMessage = updateHandler.start(update);
            // info command called
            else if (Objects.equals(newCommand, Command.INFO.getText()) || (Objects.equals(newCommand, Outcome.BACK.getText()) && companyInfoCommands.stream().anyMatch(lastChatCommand::contains)))
                sendMessage = updateHandler.providerInfo(update);
            // requests command called
            else if (Objects.equals(newCommand, Command.GET_QUERIES.getText()))
                sendMessage = updateHandler.getQueries(update);
            // new service command called
            else if (Objects.equals(newCommand, Command.NEW_SERVICE.getText()))
                sendMessage = updateHandler.requestServiceName(update);
            // my services command called
            else if (Objects.equals(newCommand, Command.MY_SERVICES.getText()) || (Objects.equals(newCommand, Outcome.BACK.getText()) && Objects.equals(lastChatCommand, Outcome.SERVICE_EDIT_REQUESTED.name())))
                sendMessage = updateHandler.getMyServices(update);
            // edit my service
            else if (Objects.equals(lastChatCommand, Command.MY_SERVICES.name()) && serviceNames.stream().anyMatch(s -> newCommand.contains(s)))
                sendMessage = updateHandler.editProviderService(update);
            // unsubscribe my service
            else if (newCommand.contains(Command.UNSUBSCRIBE_FROM_SERVICE.getText()))
                sendMessage = updateHandler.unsubscribeFromService(update);
            // unsubscribe my service
            else if (newCommand.contains(Command.SUBSCRIBE_TO_SERVICE.getText()))
                sendMessage = updateHandler.subscribeToService(update);
            //check if it is from the group
            else if (update.getChannelPost() != null && Objects.equals(update.getChannelPost().getChatId(), serviceSearChannelId))
                replyList.addAll(updateHandler.handleQuery(update));
            //if previous request was name, then request phone
            else if (Objects.equals(lastChatCommand, Outcome.NAME_REQUESTED.name()))
                sendMessage = updateHandler.requestPhone(update);
            //if previous request was phone, then request company name
            else if (Objects.equals(lastChatCommand, Outcome.PHONE_REQUESTED.name()))
                sendMessage = updateHandler.requestCompanyInfo(update);
            //request company name
            else if (newCommand.contains(Outcome.COMPANY_NAME.getText()))
                sendMessage = updateHandler.editCompanyName(update);
            //save company name
            else if (lastChatCommand.equals(Outcome.COMPANY_NAME.name()) && !newCommand.equals(Outcome.BACK.getText()))
                sendMessage = updateHandler.saveCompanyName(update);
                //request company address
            else if (newCommand.contains(Outcome.COMPANY_ADDRESS_REQUESTED.getText()))
                sendMessage = updateHandler.editCompanyAddress(update);
                //save company address
            else if (lastChatCommand.equals(Outcome.COMPANY_ADDRESS_REQUESTED.name()) && !newCommand.equals(Outcome.BACK.getText()))
                sendMessage = updateHandler.saveCompanyAddress(update);
                //request website
            else if (newCommand.contains(Outcome.WEBSITE_REQUESTED.getText()))
                sendMessage = updateHandler.editWebsite(update);
                //save website
            else if (lastChatCommand.equals(Outcome.WEBSITE_REQUESTED.name()) && !newCommand.equals(Outcome.BACK.getText()))
                sendMessage = updateHandler.saveWebsite(update);
                //request instagram
            else if (newCommand.contains(Outcome.INSTAGRAM_REQUESTED.getText()))
                sendMessage = updateHandler.editInstagram(update);
                //save instagram
            else if (lastChatCommand.equals(Outcome.INSTAGRAM_REQUESTED.name()) && !newCommand.equals(Outcome.BACK.getText()))
                sendMessage = updateHandler.saveInstagram(update);
                //request telegram
            else if (newCommand.contains(Outcome.TELEGRAM_REQUESTED.getText()))
                sendMessage = updateHandler.editTelegram(update);
                //save telegram
            else if (lastChatCommand.equals(Outcome.TELEGRAM_REQUESTED.name()) && !newCommand.equals(Outcome.BACK.getText()))
                sendMessage = updateHandler.saveTelegram(update);
                //request certificate
            else if (newCommand.contains(Outcome.CERTIFICATE_REQUESTED.getText()))
                sendMessage = updateHandler.editCertificate(update);
                //save certificate
            else if (lastChatCommand.equals(Outcome.CERTIFICATE_REQUESTED.name()) && !newCommand.equals(Outcome.BACK.getText()))
                sendMessage = updateHandler.saveCertificate(update);
            // request company name
            else if (Objects.equals(lastChatCommand, Outcome.COMPANY_INFO_REQUESTED.name()))
                sendMessage = updateHandler.requestRegion(update);
            // request region
            else if (Objects.equals(lastChatCommand, Outcome.REGION_REQUESTED.name()))
                sendMessage = updateHandler.requestServiceName(update);
            // accept query
            else if (newCommand.contains(Command.ACCEPT_QUERY.getText()))
                replyList.addAll(updateHandler.acceptQuery(update));
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
            else if (lastChatCommand.equals(Outcome.REQUEST_SERVICE_NAME.name()) || lastChatCommand.equals(Outcome.SERVICE_SEARCH_NOT_FOUND.name()) || lastChatCommand.equals(Outcome.SERVICE_SEARCH_FOUND.name()))
                sendMessage = updateHandler.searchService(update);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage = updateHandler.unknownCommand(update);
        }

        if (sendMessage != null)
            replyList.add(sendMessage);

        return replyList;
    }

    //TODO move to another service
    private static List<String> getCompanyInfoCommandTexts() {
        List<String> list = new ArrayList<>();
        list.add(Outcome.COMPANY_NAME.name());
        list.add(Outcome.COMPANY_ADDRESS_REQUESTED.name());
        list.add(Outcome.WEBSITE_REQUESTED.name());
        list.add(Outcome.INSTAGRAM_REQUESTED.name());
        list.add(Outcome.TELEGRAM_REQUESTED.name());
        list.add(Outcome.CERTIFICATE_REQUESTED.name());
        list.add(Outcome.COMPANY_INFO_REQUESTED.name());
        return list;
    }
}

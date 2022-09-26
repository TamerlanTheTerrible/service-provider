package me.timur.servicesearchtelegrambot.bot.provider.impl;

import lombok.RequiredArgsConstructor;
import me.timur.servicesearchtelegrambot.bot.client.enums.Outcome;
import me.timur.servicesearchtelegrambot.bot.provider.ProviderUpdateHandler;
import me.timur.servicesearchtelegrambot.enitity.Provider;
import me.timur.servicesearchtelegrambot.enitity.ProviderService;
import me.timur.servicesearchtelegrambot.enitity.Service;
import me.timur.servicesearchtelegrambot.repository.ProviderServiceRepository;
import me.timur.servicesearchtelegrambot.service.ChatLogService;
import me.timur.servicesearchtelegrambot.service.ProviderManager;
import me.timur.servicesearchtelegrambot.service.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.timur.servicesearchtelegrambot.bot.util.UpdateUtil.*;
import static me.timur.servicesearchtelegrambot.bot.util.UpdateUtil.chatId;

/**
 * Created by Temurbek Ismoilov on 25/09/22.
 */

@Component
@RequiredArgsConstructor
public class ProviderUpdateHandlerImpl implements ProviderUpdateHandler {

    @Value("${keyboard.size.row}")
    private Integer keyboardRowSize;

    private final ProviderManager providerManager;
    private final ChatLogService chatLogService;
    private final ServiceManager serviceManager;
    private final ProviderServiceRepository providerServiceRepository;

    @Override
    public SendMessage start(Update update) {
        //save if user doesn't exist
        Provider provider = providerManager.getOrSave(user(update));
        final String name = provider.getName();
        final SendMessage sendMessage = logAndMessage(update, String.format("Добро пожаловать, %s. Напишите названия сервиса, который вы хотите предложить", name), Outcome.START);
        sendMessage.setReplyMarkup(removeKeyboard());
        return sendMessage;
    }

    @Override
    public SendMessage searchService(Update update) {
        String command = command(update);
        SendMessage sendMessage;

        final List<Service> services = serviceManager.getAllServicesByActiveTrueAndNameLike(command);
        if (services.isEmpty()) {
            List<String> keyboardValues = new ArrayList<>();
            keyboardValues.add(Outcome.CATEGORIES.getText());
            sendMessage = logAndMessage(update, Outcome.SERVICE_SEARCH_NOT_FOUND.getText(), Outcome.SERVICE_SEARCH_NOT_FOUND);
            sendMessage.setReplyMarkup(keyboard(keyboardValues, keyboardRowSize));
        } else {
            final List<String> serviceNames = services.stream().map(Service::getNameUz).collect(Collectors.toList());
            serviceNames.add(Outcome.CATEGORIES.getText());
            sendMessage = logAndKeyboard(update, Outcome.SERVICE_SEARCH_FOUND.getText(),  serviceNames, keyboardRowSize, Outcome.SERVICE_SEARCH_FOUND);
        }

        return sendMessage;
    }

    @Override
    public SendMessage getServicesByCategoryName(Update update) {
        List<String> servicesNames = serviceManager.getServicesNamesByCategoryName(command(update));
        ArrayList<String> modifiableList = new ArrayList<>(servicesNames);
        modifiableList.add(Outcome.BACK_TO_CATEGORIES.getText());
        return logAndKeyboard(update, command(update), modifiableList, keyboardRowSize, Outcome.SERVICES);
    }

    @Override
    public SendMessage getCategories(Update update) {
        final List<String> categoryNames = serviceManager.getActiveCategoryNames();
        return logAndKeyboard(update, Outcome.CATEGORIES.getText(), categoryNames, keyboardRowSize, Outcome.CATEGORIES);
    }

    @Override
    public SendMessage saveServiceIfServiceFoundOrSearchFurther(Update update) {
        SendMessage sendMessage = null;
        Service service = serviceManager.getServiceByName(command(update));
        Provider provider = providerManager.getByUserTelegramId(tgUserId(update));

        //check if it's already registered
        Optional<ProviderService> providerServiceOpt = providerServiceRepository.findByProviderAndService(provider, service);

        if (providerServiceOpt.isPresent()) {
            sendMessage = logAndMessage(update, Outcome.PROVIDER_SERVICE_ALREADY_EXISTS.getText(), Outcome.PROVIDER_SERVICE_ALREADY_EXISTS);
        } else {
            providerServiceRepository.save(new ProviderService(provider, service));
            sendMessage = logAndMessage(update, Outcome.PROVIDER_SERVICE_SAVED.getText(), Outcome.PROVIDER_SERVICE_SAVED);

        }
        return sendMessage;
    }

    @Override
    public SendMessage unknownCommand(Update update) {
        return logAndMessage(update, Outcome.UNKNOWN_COMMAND.getText(), Outcome.UNKNOWN_COMMAND);
    }

    private SendMessage logAndMessage(Update update, String message, Outcome outcome) {
        chatLogService.log(update, outcome);
        return message(chatId(update), message);
    }

    private SendMessage logAndKeyboard(Update update, String message, List<String> serviceNames, Integer keyboardRowSize, Outcome outcome) {
        chatLogService.log(update, outcome);
        return keyboard(chatId(update), message, serviceNames, keyboardRowSize);
    }
}

package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.service.NotificationService;
import me.timur.servicesearchtelegrambot.bot.provider.service.ProviderUpdateHandler;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Command;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import me.timur.servicesearchtelegrambot.bot.provider.service.RestRequester;
import me.timur.servicesearchtelegrambot.bot.util.KeyboardUtil;
import me.timur.servicesearchtelegrambot.enitity.*;
import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import me.timur.servicesearchtelegrambot.repository.ProviderServiceRepository;
import me.timur.servicesearchtelegrambot.service.ChatLogService;
import me.timur.servicesearchtelegrambot.service.ProviderManager;
import me.timur.servicesearchtelegrambot.service.QueryService;
import me.timur.servicesearchtelegrambot.service.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.timur.servicesearchtelegrambot.bot.util.UpdateUtil.*;

/**
 * Created by Temurbek Ismoilov on 25/09/22.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderUpdateHandlerImpl implements ProviderUpdateHandler {

    private final ProviderManager providerManager;
    private final ChatLogService chatLogService;
    private final ServiceManager serviceManager;
    private final ProviderServiceRepository providerServiceRepository;
    private final QueryService queryService;
    private final NotificationService notificationService;

    @Value("${keyboard.size.row}")
    private Integer keyboardRowSize;

    @Override
    public SendMessage start(Update update) {
        //save if user doesn't exist
        Provider provider = providerManager.getOrSave(user(update));

        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        final SendMessage sendMessage = logAndKeyboard(
                update,
                "Добро пожаловать. Здесь вы можете опубликовать услуги, которые вы хотите предложить\n\n" +
                        "Для начало просим ответить на несколько вопросов о вашей фирме/деятельности. " +
                        "Ответы на вопросы необязательные и можете пропустить, если не хотите отвечать. " +
                        "Но мы рекомендуем дать на больше информации так, как это увеличит доверии к вам\n\n" +
                        "Напишите Ваше имя и фамилию",
                keyboard,
                1,
                Outcome.NAME_REQUESTED
        );
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
    public List<SendMessage> handleQuery(Update update) {
        String chatText = "";
        try {
            //get query id from chat text
            chatText = update.getChannelPost().getText();
            Long queryId = Long.valueOf(
                    chatText.substring(
                            chatText.indexOf("#") + 1,
                            chatText.indexOf(" ")
                    )
            );

            //get providers who can handle the query
            Query query = queryService.getById(queryId);
            List<Provider> providers = providerManager.findAllByService(query.getService());

            //prepare notifications for those providers
            List<SendMessage> messages = new ArrayList<>();
            for (Provider provider: providers) {
                String chatId = provider.getUser().getTelegramId().toString();
                List<String> keyboardTexts = new ArrayList<>();
                keyboardTexts.add(Command.ACCEPT_QUERY.getText() + queryId);
                keyboardTexts.add(Command.DENY_QUERY.getText());
                messages.add(keyboard(chatId, "Новый запрос #" + queryId, keyboardTexts, keyboardRowSize));
            }
            //send provider id list to channel
            SendMessage channelReply = message(
                    update.getChannelPost().getChatId().toString(),
                    "#" + queryId + " provider IDs: " + providers.stream()
                            .map(p -> String.valueOf(p.getId()))
                            .collect(Collectors.joining(", "))
            );

            messages.add(channelReply);

            return messages;
        } catch (Exception e) {
            log.error("Error while handling channel post: {}", chatText);
            log.error(e.getMessage(), e);
            return new ArrayList<SendMessage>();
        }
    }

    @Override
    public SendMessage acceptQuery(Update update) {
        //prepare reply
        SendMessage message = message(chatId(update), "Можете связаться с заказчиком: ");
        message.setReplyMarkup(KeyboardUtil.removeKeyBoard());

        //get query
        String command = command(update);
        Long queryId = Long.valueOf(
                command.substring(command.indexOf("#") + 1)
        );
        Query query = queryService.getById(queryId);

        //check if query is still active
        if (!query.getIsActive()) {
            message.setText("Клиент закрыл запрос");
            return message;
        }

        //fetch client and provider
        final User client = query.getClient();
        final Provider provider = providerManager.getByUserTelegramId(Long.valueOf(chatId(update)));

        //message to client
        if (client.getTelegramId() != null) {
            notificationService.sendNotification(
                    client.getTelegramId().toString(),
                    new ServiceProviderDTO(provider)
            );
        }

        //message to provider
        if (client.getUsername() != null)
            message.setText(message.getText() + "@" + client.getUsername());
        else
            message.setText(message.getText() + client.getPhone());
        return message;
    }

    @Override
    public SendMessage denyQuery(Update update) {
//        return logAndMessage();
        return null;
    }

    @Override
    public SendMessage requestPhone(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setName(newCommand);
            providerManager.save(provider);
        }

        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                "Напишите номер телефона в формате +9989********",
                keyboard,
                1,
                Outcome.PHONE_REQUESTED);
    }

    @Override
    public SendMessage requestCompanyName(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setPhone(newCommand);
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.COMPANY_NAME_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.COMPANY_NAME_REQUESTED);
    }

    @Override
    public SendMessage requestCompanyAddress(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setCompanyName(newCommand);
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.COMPANY_ADDRESS_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.COMPANY_ADDRESS_REQUESTED);
    }

    @Override
    public SendMessage requestWebsite(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setCompanyAddress(newCommand);
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.WEBSITE_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.WEBSITE_REQUESTED);
    }

    @Override
    public SendMessage requestInstagram(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setWebsite(newCommand);
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.INSTAGRAM_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.INSTAGRAM_REQUESTED);
    }

    @Override
    public SendMessage requestTelegram(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setInstagram(newCommand);
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.TELEGRAM_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.TELEGRAM_REQUESTED);
    }

    @Override
    public SendMessage requestCertificate(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setTelegram(newCommand);
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.CERTIFICATE_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.CERTIFICATE_REQUESTED);
    }

    @Override
    public SendMessage requestCompanyInfo(Update update) {
        final Document document = update.getMessage().getDocument();
        if (document != null) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setCertificateTgFileId(document.getFileId());
//            provider.setCertificateMyType(DocumentMimeType.findByType(document.getMimeType()));
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
;
        return logAndKeyboard(
                update,
                Outcome.COMPANY_INFO_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.COMPANY_INFO_REQUESTED);
    }

    @Override
    public SendMessage requestServiceName(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setCompanyInformation(newCommand);
            providerManager.save(provider);
        }

        SendMessage sendMessage = logAndMessage(
                update,
                Outcome.REQUEST_SERVICE_NAME.getText(),
                Outcome.REQUEST_SERVICE_NAME
        );
        sendMessage.setReplyMarkup(removeKeyboard());
        return sendMessage;
    }

    //TODO a method to save company info and carry on with saving service to be provided

    @Override
    public SendMessage getServicesByCategoryName(Update update) {
        List<String> servicesNames = serviceManager.getServicesNamesByCategoryName(command(update));
        ArrayList<String> modifiableList = new ArrayList<>(servicesNames);
        modifiableList.add(Command.BACK_TO_CATEGORIES.getText());
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

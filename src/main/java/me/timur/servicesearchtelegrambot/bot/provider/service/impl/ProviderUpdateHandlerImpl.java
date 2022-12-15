package me.timur.servicesearchtelegrambot.bot.provider.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Command;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import me.timur.servicesearchtelegrambot.bot.provider.service.NotificationService;
import me.timur.servicesearchtelegrambot.bot.provider.service.ProviderUpdateHandler;
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
import org.telegram.telegrambots.meta.api.objects.Contact;
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

    @Value("${channel.service.searcher.id.dev}")
    private String serviceSearchChannelId;

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
                    chatText.substring(chatText.indexOf("#") + 1)
            );

            //get providers who can handle the query
            Query query = queryService.getById(queryId);
            List<Provider> providers = providerManager.findAllByServiceAndActiveSubscription(query.getService());

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
                    "#" + queryId + " provider IDs: " + providers
                            .stream()
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
    public List<SendMessage> acceptQuery(Update update) {
        //client clientMsg
        SendMessage clientMsg = message(chatId(update), "Можете связаться с заказчиком: ");
        clientMsg.setReplyMarkup(KeyboardUtil.removeKeyBoard());
        //prepare reply
        List<SendMessage> messages = new ArrayList<>();
        messages.add(clientMsg);


        //get query
        String command = command(update);
        Long queryId = Long.valueOf(
                command.substring(command.indexOf("#") + 1)
        );
        Query query = queryService.getById(queryId);

        //check if query is still active
        if (!query.getIsActive()) {
            clientMsg.setText("Клиент закрыл запрос");
            return messages;
        }

        //fetch client and provider
        final User client = query.getClient();
        final Provider provider = providerManager.getByUserTelegramId(Long.valueOf(chatId(update)));

        //clientMsg to client
        if (client.getTelegramId() != null) {
            notificationService.sendNotification(
                    client.getTelegramId().toString(),
                    new ServiceProviderDTO(provider)
            );
        }

        //clientMsg to provider
        if (client.getUsername() != null)
            clientMsg.setText(clientMsg.getText() + "@" + client.getUsername());
        else
            clientMsg.setText(clientMsg.getText() + client.getPhone());

        //clientMsg to the channel
        SendMessage channelMsg = message(serviceSearchChannelId, provider.getName() + " готов обработать заказ #" + queryId);
        messages.add(channelMsg);

        return messages;
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
        final SendMessage msg = logAndMessage(update, "Поделитесь номером телефона", Outcome.PHONE_REQUESTED);
        msg.setReplyMarkup(KeyboardUtil.phoneRequest());
        return msg;
    }

    @Override
    public SendMessage requestCompanyInfo(Update update) {
        final Contact contact = update.getMessage().getContact();
        if (contact != null) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setPhone(contact.getPhoneNumber());
            providerManager.save(provider);
        }
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.SKIP.getText());
        return logAndKeyboard(
                update,
                Outcome.COMPANY_INFO_REQUESTED.getText(),
                keyboard,
                1,
                Outcome.COMPANY_INFO_REQUESTED);
    }

    @Override
    public SendMessage providerInfo(Update update) {
        Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
        List<String> keyboardValues = new ArrayList<>();

        if (provider.getCompanyName() == null)
            keyboardValues.add("➕ " + Outcome.COMPANY_NAME.getText());
        else
            keyboardValues.add("✏️ " + Outcome.COMPANY_NAME.getText() + ": " + provider.getCompanyName());

        if (provider.getCompanyAddress() == null)
            keyboardValues.add("➕ " + Outcome.COMPANY_ADDRESS_REQUESTED.getText());
        else
            keyboardValues.add("✏️ " + Outcome.COMPANY_ADDRESS_REQUESTED.getText() + ": " + provider.getCompanyAddress());

        if (provider.getWebsite() == null)
            keyboardValues.add("➕ " + Outcome.WEBSITE_REQUESTED.getText());
        else
            keyboardValues.add("✏️ " + Outcome.WEBSITE_REQUESTED.getText() + ": " + provider.getWebsite());

        if (provider.getInstagram() == null)
            keyboardValues.add("➕ " + Outcome.INSTAGRAM_REQUESTED.getText());
        else
            keyboardValues.add("✏️ " + Outcome.INSTAGRAM_REQUESTED.getText() + ": " + provider.getInstagram());

        if (provider.getTelegram() == null)
            keyboardValues.add("➕ " + Outcome.TELEGRAM_REQUESTED.getText());
        else
            keyboardValues.add("✏️ " + Outcome.TELEGRAM_REQUESTED.getText() + ": " + provider.getTelegram());

        if (provider.getCertificateTgFileId() == null)
            keyboardValues.add("➕ " + Outcome.CERTIFICATE_REQUESTED.getText());
        else
            keyboardValues.add("✏️ " + Outcome.CERTIFICATE_REQUESTED.getText() + ": Загружен ✅");

        if (provider.getCompanyInformation() == null)
            keyboardValues.add("➕ " + Outcome.COMPANY_INFO_REQUESTED.getText());
        else
            keyboardValues.add("✏️ " + Outcome.COMPANY_INFO_REQUESTED.getText() + ": " + provider.getCompanyInformation().substring(0, provider.getCompanyInformation().length() / 5));

        return logAndKeyboard(update, Outcome.MY_INFO.getText(), keyboardValues, keyboardRowSize, Outcome.MY_SERVICES);
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

    @Override
    public SendMessage editCompanyName(Update update) {
        List<String> keyboard = new ArrayList<>();
        keyboard.add(Outcome.BACK.getText());
        return logAndKeyboard(
                update,
                Outcome.COMPANY_NAME.getText(),
                keyboard,
                1,
                Outcome.COMPANY_NAME);
    }

    @Override
    public SendMessage saveCompanyName(Update update) {
        final String newCommand = command(update);
        if (!Objects.equals(newCommand, Outcome.SKIP.getText())) {
            Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
            provider.setCompanyName(newCommand);
            providerManager.save(provider);
        }
        return providerInfo(update);
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
//
//    @Override
//    public SendMessage requestCompanyInfo(Update update) {
//        Provider provider = providerManager.getByUserTelegramId(tgUserId(update));
//        if ( update.getMessage().getDocument() != null) {
//            final Document document = update.getMessage().getDocument();
//            provider.setCertificateTgFileId(document.getFileId());
//            provider.setCertificateMyType(FilenameUtils.getExtension(document.getFileName()));
//            providerManager.save(provider);
//        } else if (update.getMessage().getPhoto() != null) {
//            final List<PhotoSize> photoList = update.getMessage().getPhoto();
//            PhotoSize photo = photoList.get(photoList.size()-1);
//            provider.setCertificateTgFileId(photo.getFileId());
//            provider.setCertificateMyType("jpeg");
//            providerManager.save(provider);
//        }
//        List<String> keyboard = new ArrayList<>();
//        keyboard.add(Outcome.SKIP.getText());
//;
//        return logAndKeyboard(
//                update,
//                Outcome.COMPANY_INFO_REQUESTED.getText(),
//                keyboard,
//                1,
//                Outcome.COMPANY_INFO_REQUESTED);
//    }

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
            providerServiceRepository.save(new ProviderService(provider, service, false));
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

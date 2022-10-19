package me.timur.servicesearchtelegrambot.bot.util;

import me.timur.servicesearchtelegrambot.model.dto.UserDTO;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;
import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 15/08/22.
 */

public class UpdateUtil {

    public static String command(Update update) {
        return Objects.nonNull(update.getMessage())
                ? update.getMessage().getText()
                : update.getChannelPost().getText();
    }

    public static String chatId(Update update) {
        return Objects.nonNull(update.getMessage())
                ? update.getMessage().getChatId().toString()
                : update.getChannelPost().getChatId().toString();
    }

    public static Long tgUserId(Update update) {
        return update.getMessage() != null ? update.getMessage().getFrom().getId() : -1;
    }

    public static UserDTO user(Update update) {
        UserDTO dto = new UserDTO();
        final User tgUser = update.getMessage().getFrom();
        dto.setTelegramId(tgUser.getId());
        dto.setUsername(tgUser.getUserName());
        dto.setFirstname(tgUser.getFirstName());
        dto.setLastname(tgUser.getLastName());
        dto.setPhone(dto.getPhone());
        return dto;
    }

    public static  SendMessage message(String chatId, String text) {
        return new SendMessage(chatId, text);
    }

    public static  SendMessage keyboard(String chatId, String text, List<String> buttonValues,Integer keyboardRowSize) {
        SendMessage sendMessage = message(chatId, text);
        if (keyboardRowSize == 0) {
            sendMessage.setReplyMarkup(KeyboardUtil.removeKeyBoard());
        } else {
            sendMessage.setReplyMarkup(KeyboardUtil.keyboard(buttonValues, keyboardRowSize));
        }
        return sendMessage;
    }

    public static ReplyKeyboardMarkup keyboard(List<String> buttonValues, Integer keyboardRowSize) {
        return KeyboardUtil.keyboard(buttonValues, keyboardRowSize);
    }

    public static ReplyKeyboardRemove removeKeyboard() {
        return KeyboardUtil.removeKeyBoard();
    }
}

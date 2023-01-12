package me.timur.servicesearchtelegrambot.bot.provider.enums;

import lombok.Getter;

/**
 * Created by Temurbek Ismoilov on 06/08/22.
 */

@Getter
public enum Command {
    START("/start"),
    INFO("/info"),
    NEW_SERVICE("/newservice"),
    MY_SERVICES("/services"),
    GET_QUERIES("/requests"),
    SUBSCRIBE_TO_SERVICE("➕ Подписаться"),
    UNSUBSCRIBE_FROM_SERVICE("\uD83D\uDDD1 Отписаться"),
    ACCEPT_QUERY("Принять запрос #"),
    DENY_QUERY("Отказать"),
    BACK_TO_CATEGORIES("Все категории")
    ;

    private final String text;

    Command(String s) {
        this.text = s;
    }

}

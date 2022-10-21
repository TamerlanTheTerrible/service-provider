package me.timur.servicesearchtelegrambot.bot.provider.enums;

import lombok.Getter;

/**
 * Created by Temurbek Ismoilov on 19/08/22.
 */

@Getter
public enum Outcome {
    START("/start"),
    SERVICE_SEARCH_FOUND("Возможно вы имели ввиду... \nЕсли не нашли, то что искали напишите названия сервиса еще раз или выберите услугу из общего списка"),
    SERVICE_SEARCH_NOT_FOUND("Не удалось найти сервис. Попробуйте еще раз или выберите из списка"),
    QUERY_SAVED("Ваша заявка принято. С вами свяжутся, как только найдется нужный сервис провайдер"),
    CATEGORIES("Выбрать из общего списка"),
    SERVICES("Выбрать из общего списка"),
    ACCEPT_QUERY("Принять запрос #"),
    DENY_QUERY("Отказать"),
    BACK_TO_CATEGORIES("Все категории"),

    PROVIDER_SERVICE_ALREADY_EXISTS("Этот сервис у Вас уже зарегистрирован"),
    PROVIDER_SERVICE_SAVED("Сервис сохранён"),
    RECEIVE_QUERY_NOTIFICATION("Запрос"),

    UNKNOWN_COMMAND("Неизвестная команда. Попробуйте еще раз")
    ;

    private final String text;

    Outcome(String s) {
        this.text = s;
    }
}

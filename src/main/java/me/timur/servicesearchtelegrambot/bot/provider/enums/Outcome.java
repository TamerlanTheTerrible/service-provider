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
    CATEGORIES("Выбрать из общего списка"),
    SERVICES("Выбрать из общего списка"),
    QUERY_ACCEPTED("Запрос принят"),
    QUERY_DENIED("Запрос отказан"),
//    BACK_TO_CATEGORIES("Все категории"),

    PROVIDER_SERVICE_ALREADY_EXISTS("Этот сервис у Вас уже зарегистрирован"),
    PROVIDER_SERVICE_SAVED("Сервис сохранён"),
    RECEIVE_QUERY_NOTIFICATION("Запрос"),

    NAME_REQUESTED("Имя фамилия"),
    PHONE_REQUESTED("Телефонный номер"),
    COMPANY_NAME_REQUESTED("Название фирмы"),
    COMPANY_ADDRESS_REQUESTED("Адрес фирмы"),
    WEBSITE_REQUESTED("Вэб-сайт"),
    INSTAGRAM_REQUESTED("Инстаграм аккаунт"),
    TELEGRAM_REQUESTED("Телеграм аккаунт/группа/канал"),
    CERTIFICATE_REQUESTED("Загрузите сертификат"),
    COMPANY_INFO_REQUESTED("Напишите немного о компании/деятельности (максимум 255 символов)"),
    SKIP("➡️Пропустить"),

    UNKNOWN_COMMAND("Неизвестная команда. Попробуйте еще раз"),
    REQUEST_SERVICE_NAME("Напишите названия сервиса, который вы хотите предложить");

    private final String text;

    Outcome(String s) {
        this.text = s;
    }
}

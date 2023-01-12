package me.timur.servicesearchtelegrambot.bot.provider.enums;

import lombok.Getter;

/**
 * Created by Temurbek Ismoilov on 19/08/22.
 */

@Getter
public enum Outcome {
    START("/start"),
    REQUEST_SERVICE_NAME("Напишите названия сервиса, который вы хотите предложить"),
    SERVICE_SEARCH_FOUND("Возможно вы имели ввиду... \nЕсли не нашли, то что искали напишите названия сервиса еще раз или выберите услугу из общего списка"),
    SERVICE_SEARCH_NOT_FOUND("Не удалось найти сервис. Попробуйте еще раз или выберите из списка"),
    CATEGORIES("Выбрать из общего списка"),
    SERVICES("Выбрать из общего списка"),

    MY_SERVICES("Мои услуги (\uD83D\uDFE2-активные, \uD83D\uDD34-не активные)"),
    COMPANY_INFO("/info"),
    SERVICE_EDIT_REQUESTED("Подписаться/Отписаться"),
    SERVICE_SUBSCRIBED("Успешно подписались на сервис"),
    SERVICE_UNSUBSCRIBED("Успешно отписались от сервиса"),

    QUERY_ACCEPTED("Запрос принят"),
    QUERY_DENIED("Запрос отказан"),
    QUERY_NOTIFIED(""),
    GET_QUERIES("/requests"),
    QUERY_NOT_FOUND("Нет актуальных запросов"),
    QUERY_FOUND("Актуальные запросы"),
//    BACK_TO_CATEGORIES("Все категории"),

    PROVIDER_SERVICE_ALREADY_EXISTS("Этот сервис у Вас уже зарегистрирован"),
    PROVIDER_SERVICE_SAVED("Сервис сохранён"),
    RECEIVE_QUERY_NOTIFICATION("Запрос"),

    MY_INFO("Моя анкета"),
    NAME_REQUESTED("Имя фамилия"),
    PHONE_REQUESTED("Телефонный номер"),
    COMPANY_NAME("Название фирмы"),
    COMPANY_ADDRESS_REQUESTED("Адрес фирмы"),
    WEBSITE_REQUESTED("Вэб-сайт"),
    INSTAGRAM_REQUESTED("Инстаграм аккаунт"),
    TELEGRAM_REQUESTED("Телеграм аккаунт/группа/канал"),
    CERTIFICATE_REQUESTED("Загрузите сертификат"),
    COMPANY_INFO_REQUESTED("Напишите немного о деятельности "),
    SKIP("➡️Пропустить"),
    BACK("\uD83D\uDD19 Назад"),

    UNKNOWN_COMMAND("Неизвестная команда. Попробуйте еще раз"),

    REGION_REQUESTED("Выберите регион вашего проживания")
    ;
    private final String text;

    Outcome(String s) {
        this.text = s;
    }
}

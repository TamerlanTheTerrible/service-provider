package me.timur.servicesearchtelegrambot.bot.provider.enums;

import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 12/01/23.
 */

public enum Region {
    TOSHKENT("ТАШКЕНТ"),
    SIRDARYO("СЫРДАРЬЯ"),
    JIZZAX("ДЖИЗАК"),
    SAMARQAND("САМАРКАНД"),
    FARGONA("ФЕРГАНА"),
    ANDIJON("АНДИЖАН"),
    NAMANGAN("НАМАНГАН"),
    BUXORO("БУХОРО"),
    NAVOI("НАВОИ"),
    QASHQADARYO("КАШКАДАРЬЯ"),
    SURXONDARYO("СУРХАНДАРЬЯ"),
    XORAZM("ХОРАЗМ"),
    QORAQALPOGISTON("КАРАКАЛПАКСТАН");

    public final String russian;
    public static Region getByRussian(String russian) {
        final Region[] values = Region.values();
        for(Region region: values) {
            if (Objects.equals(region.russian, russian)) {
                return region;
            }
        }
        return null;
    }

    Region(String russian) {
        this.russian = russian;
    }
}

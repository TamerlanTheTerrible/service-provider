package me.timur.servicesearchtelegrambot.bot.provider.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Temurbek Ismoilov on 20/11/22.
 */

//TODO delete?
public enum DocumentMimeType {
    IMAGE_PNG("image/png"),
    APPLICATION_PDF("application/pdf")
    ;

    private String type;

    DocumentMimeType(String type) {
        this.type = type;
    }

    public static DocumentMimeType findByType(String type) {
        final List<DocumentMimeType> result = Arrays
                .stream(DocumentMimeType.values())
                .filter(t -> Objects.equals(t.type, type))
                .collect(Collectors.toList());
        return result.size() == 1 ? result.get(0) : IMAGE_PNG;
    }
}

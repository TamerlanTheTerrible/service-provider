package me.timur.servicesearchtelegrambot.enitity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

@Data
public class ProviderDto implements Serializable {
    private final Long id;
    private final String name;
    private final String phone;
    private final String companyName;
    private final String companyAddress;
    private final String website;
    private final String instagram;
    private final String telegram;
    private final String certificateTgFileId;
    private final String companyInformation;
    private final Boolean isActive;
}

package me.timur.servicesearchtelegrambot.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.timur.servicesearchtelegrambot.enitity.Provider;
import me.timur.servicesearchtelegrambot.util.DateUtil;

import java.sql.Timestamp;

/**
 * Created by Temurbek Ismoilov on 02/05/22.
 */

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProviderDTO extends BaseDTO {
    private Long id;
    @JsonProperty("date_created")
    @JsonFormat(pattern = DateUtil.DATE_TIME_PATTERN) @JsonDeserialize(using = DateDeserializers.TimestampDeserializer.class)
    private Timestamp dateCreated;
    private UserDTO user;
    private String name;
    private String phone;
    private String companyName;
    private String companyAddress;
    private String webSite;
    private String instagram;
    private String telegram;
    private String description;
    private String certificateTgFileId;
    private ServiceDTO service;
    @JsonProperty("is_active")
    private Boolean isActive;

    public ServiceProviderDTO(Provider provider) {
        this.id = provider.getId();
        this.user = new UserDTO(provider.getUser());
        this.dateCreated = provider.getDateCreated();
        this.name = provider.getName();
        this.phone = provider.getPhone();
        this.companyAddress = provider.getCompanyAddress();
        this.companyName = provider.getCompanyName();
        this.webSite = provider.getWebsite();
        this.instagram = provider.getInstagram();
        this.telegram = provider.getTelegram();
        this.description = provider.getCompanyInformation();
        this.certificateTgFileId = provider.getCertificateTgFileId();
//        this.service = new ServiceDTO(provider.getService());
        this.isActive = provider.getIsActive();
    }
}

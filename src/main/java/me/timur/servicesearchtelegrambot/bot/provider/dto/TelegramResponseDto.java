package me.timur.servicesearchtelegrambot.bot.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramResponseDto<T> {
    String ok;
    @JsonProperty("error_code")
    Integer errorCode;
    String description;
    T result;
}

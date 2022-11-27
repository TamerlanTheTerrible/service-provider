package me.timur.servicesearchtelegrambot.bot.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by Temurbek Ismoilov on 27/11/22.
 */

@Data
public class FileDto {
    @JsonProperty("file_id")
    String fileId;
    @JsonProperty("file_unique_id")
    String fileUniqueId;
    @JsonProperty("file_size")
    Integer fileSize;
    @JsonProperty("file_path")
    String filePath;
}

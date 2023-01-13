package me.timur.servicesearchtelegrambot.repository;

import me.timur.servicesearchtelegrambot.bot.provider.enums.ChatLogType;
import me.timur.servicesearchtelegrambot.enitity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Temurbek Ismoilov on 03/08/22.
 */

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    Optional<ChatLog> findTopByTgChatIdAndLogTypeOrderByIdDesc(String tgChatId, ChatLogType type);
}

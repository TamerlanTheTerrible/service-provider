package me.timur.servicesearchtelegrambot.enitity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.timur.servicesearchtelegrambot.bot.provider.enums.ChatLogType;
import me.timur.servicesearchtelegrambot.bot.provider.enums.Outcome;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

import static me.timur.servicesearchtelegrambot.bot.util.UpdateUtil.*;

/**
 * Created by Temurbek Ismoilov on 03/08/22.
 */

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "chat_log")
public class ChatLog extends BaseEntity{
    @Column(name = "id")
    private Long id;

    @Column(name = "tg_user_id")
    private Long tgUserId;

    @Column(name = "tg_chat_id")
    private String tgChatId;

    @Column(name = "command")
    private String command;

    @Column(name = "outcome")
    @Enumerated(value = EnumType.STRING)
    private Outcome outcome;

    @Column(name = "log_type")
    @Enumerated(value = EnumType.STRING)
    private ChatLogType logType;

    public ChatLog(Update update, Outcome outcome) {
        this.tgUserId = tgUserId(update);
        this.tgChatId = chatId(update);
        this.command = command(update);
        this.outcome = outcome;
        this.logType = ChatLogType.PROVIDER;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatLog)) return false;

        ChatLog chatLog = (ChatLog) o;

        return getId().equals(chatLog.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}

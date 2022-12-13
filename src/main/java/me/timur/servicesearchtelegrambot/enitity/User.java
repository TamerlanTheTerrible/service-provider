package me.timur.servicesearchtelegrambot.enitity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.timur.servicesearchtelegrambot.model.dto.UserDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 25/04/22.
 */

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "telegram_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public User(UserDTO dto) {
        this.telegramId = dto.getTelegramId();
        this.username = dto.getUsername();
        this.firstname = dto.getFirstname();
        this.lastname = dto.getLastname();
        if (Objects.nonNull(dto.getPhone())) {
            this.phone = dto.getPhone().replace(" ", "").replace("+", "");
        }
        this.isActive = true;
    }

    @Override
    public String toString() {
        return "User{" +
                "telegramId=" + telegramId +
                ", username='" + username + '\'' +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

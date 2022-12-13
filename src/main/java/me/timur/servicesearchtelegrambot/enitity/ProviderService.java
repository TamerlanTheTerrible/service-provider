package me.timur.servicesearchtelegrambot.enitity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 25/09/22.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "provider_service")
public class ProviderService extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "active")
    private Boolean active;

    @Override
    public String toString() {
        return "ProviderService{" +
                "provider=" + provider +
                ", service=" + service +
                ", active=" + active +
                '}';
    }
}

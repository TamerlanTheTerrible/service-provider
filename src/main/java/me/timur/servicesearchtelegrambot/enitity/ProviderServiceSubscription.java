package me.timur.servicesearchtelegrambot.enitity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 25/04/22.
 */

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "provider_service_subscription")
public class ProviderServiceSubscription extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "provider_service_id", nullable = false)
    private ProviderService providerService;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProviderServiceSubscription that = (ProviderServiceSubscription) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProviderServiceSubscription{" +
                "providerService=" + providerService +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}

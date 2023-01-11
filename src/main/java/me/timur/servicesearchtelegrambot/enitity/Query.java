package me.timur.servicesearchtelegrambot.enitity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Temurbek Ismoilov on 25/04/22.
 */

@Getter @Setter @RequiredArgsConstructor
@Entity
@Table(name = "query")
public class Query extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "client_user_id", nullable = false)
    private User client;

    @OneToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @OneToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "status")
    private QueryStatus status;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "comment")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Query)) return false;

        Query query = (Query) o;

        return getClient().equals(query.getClient());
    }

    @Override
    public int hashCode() {
        return getClient().hashCode();
    }

    public Query(User client, Provider provider, Service service) {
        this.client = client;
        this.provider = provider;
        this.service = service;
    }

    public Query(User client, Service service) {
        this.client = client;
        this.service = service;
    }
}

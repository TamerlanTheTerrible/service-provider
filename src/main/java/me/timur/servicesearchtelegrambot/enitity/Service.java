package me.timur.servicesearchtelegrambot.enitity;

import lombok.*;
import me.timur.servicesearchtelegrambot.model.dto.ServiceDTO;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 25/04/22.
 */

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "service")
public class Service extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "name_ru")
    private String nameRu;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    public Service(ServiceDTO dto, ServiceCategory category) {
        this.name = dto.getName().trim().toUpperCase();
        this.nameUz = dto.getNameUz();
        this.nameRu = dto.getNameRu();
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Service service = (Service) o;
        return getId() != null && Objects.equals(getId(), service.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", nameUz='" + nameUz + '\'' +
                ", nameRu='" + nameRu + '\'' +
                ", category=" + category +
                ", active=" + active +
                '}';
    }
}

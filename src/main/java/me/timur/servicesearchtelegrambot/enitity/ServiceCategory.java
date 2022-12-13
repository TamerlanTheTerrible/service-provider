package me.timur.servicesearchtelegrambot.enitity;

import lombok.*;
import me.timur.servicesearchtelegrambot.model.dto.ServiceCategoryDTO;
import org.hibernate.Hibernate;

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
@Table(name = "service_category")
public class ServiceCategory extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public ServiceCategory(ServiceCategoryDTO serviceCategoryDto) {
        this.name = serviceCategoryDto.getName().trim().toUpperCase();
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ServiceCategory category = (ServiceCategory) o;
        return getId() != null && Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ServiceCategory{" +
                "name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}

package me.timur.servicesearchtelegrambot.enitity;

import lombok.*;
import me.timur.servicesearchtelegrambot.bot.provider.enums.DocumentMimeType;
import org.checkerframework.checker.compilermsgs.qual.UnknownCompilerMessageKey;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by Temurbek Ismoilov on 02/05/22.
 */

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "provider")
public class Provider extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "website")
    private String website;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "telegram")
    private String telegram;

    @Column(name = "certificate_tg_file_id")
    private String certificateTgFileId;

//    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_mime_type")
    private String certificateMyType;

    @Column(name = "company_information")
    private String companyInformation;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "provider", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private List<ProviderService> services;

    public Provider(User user) {
        this.user = user;
        this.isActive = true;
    }

//    public String getUserName() {
//        if (this.user == null) return "";
//        return Objects.nonNull(this.user.getFirstname()) ? this.user.getFirstname()
//                : Objects.nonNull(this.user.getLastname()) ? this.user.getLastname()
//                : Objects.nonNull(this.user.getUsername()) ? this.user.getUsername()
//                : "друг";
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Provider provider = (Provider) o;
        return getId() != null && Objects.equals(getId(), provider.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Provider{" +
                "user=" + user +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", website='" + website + '\'' +
                ", instagram='" + instagram + '\'' +
                ", telegram='" + telegram + '\'' +
                ", certificateTgFileId='" + certificateTgFileId + '\'' +
                ", companyInformation='" + companyInformation + '\'' +
                ", isActive=" + isActive +
                ", services=" + services +
                '}';
    }
}

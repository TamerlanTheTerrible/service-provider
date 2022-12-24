package me.timur.servicesearchtelegrambot.repository;

import me.timur.servicesearchtelegrambot.enitity.Provider;
import me.timur.servicesearchtelegrambot.enitity.ProviderService;
import me.timur.servicesearchtelegrambot.enitity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Temurbek Ismoilov on 26/09/22.
 */

@Repository
public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long> {
    List<ProviderService> findAllByProviderUserTelegramId(Long tgUserId);
    Optional<ProviderService> findByProviderAndService(Provider provider, Service service);
    Optional<ProviderService> findByService(Service service);
}

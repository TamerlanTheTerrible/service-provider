package me.timur.servicesearchtelegrambot.repository;

import me.timur.servicesearchtelegrambot.enitity.Service;
import me.timur.servicesearchtelegrambot.enitity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByIdAndIsActiveTrue(Long id);
//    List<Provider> findAllByIdInAndIsActiveTrue(List<Long> ids);
//    List<Provider> findAllByServiceAndIsActiveTrue(Service service);
    Optional<Provider> findByUserTelegramId(Long tgID);
}
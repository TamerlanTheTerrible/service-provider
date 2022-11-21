package me.timur.servicesearchtelegrambot.service;

import me.timur.servicesearchtelegrambot.enitity.Service;
import me.timur.servicesearchtelegrambot.enitity.Provider;
import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import me.timur.servicesearchtelegrambot.model.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 16/05/22.
 */

@Transactional
public interface ProviderManager {

    Provider getById(Long providerId);

    Provider getActiveById(Long id);

    Provider getByUserTelegramId(Long telegramId);

    List<Provider> getAll();

    void update(Long providerId, ServiceProviderDTO dto);

    void delete(Long providerId);

    List<Provider> findAllByService(Service service);

    Provider getOrSave(UserDTO user);

    Provider save(Provider provider);
}

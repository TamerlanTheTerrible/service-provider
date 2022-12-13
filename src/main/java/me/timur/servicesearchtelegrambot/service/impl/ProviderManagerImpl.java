package me.timur.servicesearchtelegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import me.timur.servicesearchtelegrambot.enitity.Provider;
import me.timur.servicesearchtelegrambot.enitity.ProviderService;
import me.timur.servicesearchtelegrambot.enitity.User;
import me.timur.servicesearchtelegrambot.exception.ResourceNotFoundException;
import me.timur.servicesearchtelegrambot.model.dto.ServiceProviderDTO;
import me.timur.servicesearchtelegrambot.model.dto.UserDTO;
import me.timur.servicesearchtelegrambot.repository.ProviderRepository;
import me.timur.servicesearchtelegrambot.repository.ProviderServiceRepository;
import me.timur.servicesearchtelegrambot.repository.ProviderServiceSubscriptionRepository;
import me.timur.servicesearchtelegrambot.service.ServiceManager;
import me.timur.servicesearchtelegrambot.service.ProviderManager;
import me.timur.servicesearchtelegrambot.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Created by Temurbek Ismoilov on 16/05/22.
 */

@Service
@RequiredArgsConstructor
public class ProviderManagerImpl implements ProviderManager {

    private final ProviderRepository providerRepository;
    private final ServiceManager serviceManager;
    private final UserService userService;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderServiceSubscriptionRepository subscriptionRepository;

    @Override
    public Provider getById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(format("Could not find provider with id %s", id)));
    }

    @Override
    public Provider getActiveById(Long id) {
        return providerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(format("Could not find provider with id %s", id)));
    }

    @Override
    public Provider getByUserTelegramId(Long telegramId) {
        return providerRepository.findByUserTelegramId(telegramId)
                .orElseThrow(() -> new ResourceNotFoundException(format("Could not find provider with telegramid %s", telegramId)));
    }

    @Override
    public List<Provider> getAll() {
        return providerRepository.findAll();
    }

    @Override
    public void update(Long providerId, ServiceProviderDTO dto) {
        Provider provider = getById(providerId);

        final me.timur.servicesearchtelegrambot.enitity.Service service = serviceManager.getService(dto.getService().getId());
//        provider.setService(service);

        providerRepository.save(provider);
    }

    @Override
    public void delete(Long providerId) {
        Provider provider = getById(providerId);
        provider.setIsActive(false);
        providerRepository.save(provider);
    }

    @Override
    public List<Provider> findAllByServiceAndActiveSubscription(me.timur.servicesearchtelegrambot.enitity.Service service) {
        return subscriptionRepository
                .findByProviderServiceServiceAndEndDateAfterOrderByStartDateDesc(service, LocalDate.now())
                .stream()
                .filter(s -> s.getProviderService().getActive())
                .map(s -> s.getProviderService().getProvider())
                .collect(Collectors.toList());

//        List<Long> providerIdList = providerServiceRepository.findAllByServiceAndActiveTrue(service)
//                .stream().map(ps -> ps.getProvider().getId()).collect(Collectors.toList());
//
//        return providerRepository.findAllByIdInAndIsActiveTrue(providerIdList);
    }

    @Override
    public Provider getOrSave(UserDTO userDTO) {
        User user = userService.getOrSave(userDTO);
        Optional<Provider> providerOptional = providerRepository.findByUserTelegramId(user.getTelegramId());
        return providerOptional.orElseGet(() -> providerRepository.save(new Provider(user)));
    }

    @Override
    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }
}

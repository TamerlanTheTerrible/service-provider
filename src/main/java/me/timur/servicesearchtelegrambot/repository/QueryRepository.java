package me.timur.servicesearchtelegrambot.repository;

import me.timur.servicesearchtelegrambot.bot.provider.enums.Region;
import me.timur.servicesearchtelegrambot.enitity.Query;
import me.timur.servicesearchtelegrambot.enitity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {
    List<Query> findAllByServiceInAndClientRegionAndIsActiveTrue(List<Service> services, Region region);
}
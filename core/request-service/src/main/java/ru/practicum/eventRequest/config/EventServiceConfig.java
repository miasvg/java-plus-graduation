package ru.practicum.eventRequest.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.practicum.feign.FeignEventClient;
import ru.practicum.feign.FeignUserClient;

@Configuration
@EnableFeignClients(clients = {FeignUserClient.class, FeignEventClient.class})
public class EventServiceConfig {
}

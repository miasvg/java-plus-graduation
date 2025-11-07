package ru.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.practicum.feign.FeignUserClient;

@Configuration
@EnableFeignClients(clients = {FeignUserClient.class})
public class EventServiceConfig {
}

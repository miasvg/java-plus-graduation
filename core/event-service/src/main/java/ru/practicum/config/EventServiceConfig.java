package ru.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.practicum.feign.FeignRequestLikeClientFallback;
import ru.practicum.feign.FeignUserClient;
import ru.practicum.feign.FeingLikeRequestClient;

@Configuration
@EnableFeignClients(clients = {FeignUserClient.class, FeingLikeRequestClient.class})
public class EventServiceConfig {
}

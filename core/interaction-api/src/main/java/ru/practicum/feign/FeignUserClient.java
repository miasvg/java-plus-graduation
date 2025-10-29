package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;

import java.util.Optional;

@FeignClient(name = "USER-SERVICE", fallback = FeignUserClientFallback.class)
public interface FeignUserClient {


    @GetMapping("/admin/users/{id}")
    Optional<UserDto> getUserById(@PathVariable Long id);
}

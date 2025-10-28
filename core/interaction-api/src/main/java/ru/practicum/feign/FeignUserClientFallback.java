package ru.practicum.feign;

import ru.practicum.dto.UserDto;

import java.util.Optional;

public class FeignUserClientFallback implements FeignUserClient{

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return Optional.empty();
    }
}

package ru.practicum.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя с параметрами {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") @Min(0) Long from,
                                  @RequestParam(required = false, defaultValue = "10") @Min(1) Long size) {
        log.info("Запрос на получение всех пользователей удовлетворяющему условию: ids {}, from {}, size {}",
                ids, from, size);
        return userService.getUsers(ids, from, size);
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }


    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@PathVariable Long id) {
        return userService.gtUserById(id);
    }


}
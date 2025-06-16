package ru.practicum.user.mappers;

import ru.practicum.user.model.User;
import ru.practicum.user.model.UserDto.UserDto;

public class UserMapper {
    public static User mapToUser(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto mapToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}

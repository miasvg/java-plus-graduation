package service;

import model.UserDto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, Long from, Long size);

    void deleteUser(Long userId);
}

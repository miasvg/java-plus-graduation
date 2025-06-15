package service.serviceImpl;

import exeption.EmailMustBeUniqueException;
import exeption.UserNotExistException;
import lombok.RequiredArgsConstructor;
import mappers.UserMapper;
import model.User;
import model.UserDto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.UserService;


import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

import static mappers.UserMapper.mapToUser;
import static mappers.UserMapper.mapToUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailMustBeUniqueException(userDto.getEmail());
        }
        return mapToUserDto(userRepository.save(mapToUser(userDto)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Long from, Long size) {
        List<User> users;

        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findByIdIn(ids);
        } else {
            Page<User> page = userRepository.findByIdAfter(
                    from,
                    (Pageable) PageRequest.of(0, size.intValue(), Sort.by("id"))
            );
            users = page.getContent();
        }

        return users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException(userId);
        }
        userRepository.deleteById(userId);
    }
}

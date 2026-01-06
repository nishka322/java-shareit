package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BaseUserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userUpdates);

    UserDto getUserById(Long userId);

    User getUserEntityById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);
}
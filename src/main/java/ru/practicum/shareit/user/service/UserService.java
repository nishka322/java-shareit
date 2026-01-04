package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements BaseUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(@Qualifier("dbRepo") UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя с email: {}", userDto.getEmail());

        validateUser(userDto);
        checkEmailExists(userDto.getEmail());
        User user = userMapper.toEntity(userDto);
        User createdUser = userRepository.createUser(user);

        log.info("Пользователь создан с ID: {}", createdUser.getId());
        return userMapper.toDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userUpdates) {
        log.info("Обновление пользователя с ID: {}", userId);

        User existingUser = userRepository.getUser(userId);

        if (userUpdates.getName() != null && !userUpdates.getName().trim().isEmpty()) {
            existingUser.setName(userUpdates.getName());
        }

        if (userUpdates.getEmail() != null && !userUpdates.getEmail().trim().isEmpty()) {
            if (!isValidEmail(userUpdates.getEmail())) {
                throw new IllegalArgumentException("Некорректный формат email");
            }

            if (!existingUser.getEmail().equals(userUpdates.getEmail())) {
                checkEmailExists(userUpdates.getEmail());
            }

            existingUser.setEmail(userUpdates.getEmail());
        }

        User updatedUser = userRepository.editUser(existingUser, userId);
        log.info("Пользователь с ID: {} успешно обновлен", userId);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Получение пользователя с ID: {}", userId);
        User user = userRepository.getUser(userId);
        return userMapper.toDto(user);
    }

    @Override
    public User getUserEntityById(Long userId) {
        log.info("Получение сущности пользователя с ID: {}", userId);
        return userRepository.getUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);

        userRepository.getUser(userId);

        userRepository.removeUser(userId);
        log.info("Пользователь с ID: {} успешно удален", userId);
    }

    public Optional<User> findById(Long userId) {
        try {
            User user = userRepository.getUser(userId);
            return Optional.of(user);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private void validateUser(UserDto user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email пользователя не может быть пустым");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Некорректный формат email");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с email " + email + " уже существует");
        }
    }
}
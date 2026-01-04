package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements BaseUserService {

    @Qualifier("inMemoryRepo")
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя с email: {}", user.getEmail());

        validateUser(user);
        User createdUser = userRepository.createUser(user);

        log.info("Пользователь создан с ID: {}", createdUser.getId());
        return createdUser;
    }

    @Override
    public User updateUser(Long userId, User userUpdates) {
        log.info("Обновление пользователя с ID: {}", userId);

        User existingUser = getUserById(userId);

        User updatedUser = prepareUpdatedUser(existingUser, userUpdates);

        User result = userRepository.editUser(updatedUser, userId);

        log.info("Пользователь с ID: {} успешно обновлен", userId);
        return result;
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Получение пользователя с ID: {}", userId);
        return userRepository.getUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return new ArrayList<>();
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);

        getUserById(userId);

        userRepository.removeUser(userId);
        log.info("Пользователь с ID: {} успешно удален", userId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email пользователя не может быть пустым");
        }

        if (isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Некорректный формат email");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
    }

    private boolean isValidEmail(String email) {
        return email == null || !email.contains("@");
    }

    private User prepareUpdatedUser(User existingUser, User updates) {
        User updatedUser = new User();
        updatedUser.setId(existingUser.getId());

        if (updates.getName() != null && !updates.getName().trim().isEmpty()) {
            updatedUser.setName(updates.getName());
        } else {
            updatedUser.setName(existingUser.getName());
        }

        if (updates.getEmail() != null && !updates.getEmail().trim().isEmpty()) {
            if (isValidEmail(updates.getEmail())) {
                throw new IllegalArgumentException("Некорректный формат email");
            }
            updatedUser.setEmail(updates.getEmail());
        } else {
            updatedUser.setEmail(existingUser.getEmail());
        }

        return updatedUser;
    }
}
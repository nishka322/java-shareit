package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements BaseUserService {
    @Qualifier("inMemoryRepo")
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    @Override
    public User editUser(User user, long userId) {
        return userRepository.editUser(user, userId);
    }

    @Override
    public User getUser(long userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public void removeUser(long userId) {
        userRepository.removeUser(userId);
    }

}
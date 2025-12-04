package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface BaseUserService {
    User createUser(User user);

    User editUser(User user, long userId);

    User getUser(long userId);

    void removeUser(long userId);
}
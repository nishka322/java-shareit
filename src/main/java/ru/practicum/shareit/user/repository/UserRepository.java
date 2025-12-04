package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    User createUser(User user);

    User editUser(User user, long userId);

    User getUser(long id);

    void removeUser(long id);
}
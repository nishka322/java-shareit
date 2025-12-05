package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);
    User editUser(User user, long userId);
    User getUser(long id);
    void removeUser(long id);
    List<User> findAll();
    boolean existsById(long id);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
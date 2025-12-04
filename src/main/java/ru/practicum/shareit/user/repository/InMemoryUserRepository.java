package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.AlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
@Qualifier("inMemoryRepo")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> usersStorage = new HashMap<>();

    private long maxId = 0;

    @Override
    public User createUser(User user) {
        checkEmail(user.getEmail());
        long id = generateId();
        user.setId(id);
        usersStorage.put(id, user);
        log.info("User was successful added to memory");
        return user;
    }

    @Override
    public User editUser(User user, long userId) {
        User oldUser = usersStorage.get(userId);

        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        if (!user.getEmail().equals(oldUser.getEmail())) {
            checkEmail(user.getEmail());
        }
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        user.setId(userId);
        usersStorage.put(userId, user);
        log.info("User was updated.");
        return user;
    }

    @Override
    public User getUser(long id) {
        if (!usersStorage.containsKey(id)) {
            log.debug("Id {} wasn't found in memory.", id);
            throw new NotFoundException("User with id = " + id + " not found.");
        }
        return usersStorage.get(id);
    }

    @Override
    public void removeUser(long id) {
        usersStorage.remove(id);
    }

    private long generateId() {
        return ++maxId;
    }

    private void checkEmail(String email) {
        List<User> users = usersStorage.values().stream().filter(u -> u.getEmail().equals(email)).toList();
        if (!users.isEmpty()) {
            log.warn("Try to add new user with existing email");
            throw new AlreadyExistsException("Email already exist.");
        }
    }
}
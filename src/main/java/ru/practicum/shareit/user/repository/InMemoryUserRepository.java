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
        if (!usersStorage.containsKey(id)) {
            throw new NotFoundException("User with id = " + id + " not found.");
        }
        usersStorage.remove(id);
        log.info("User with id {} was removed", id);
    }

    public List<User> findAll() {
        return new ArrayList<>(usersStorage.values());
    }

    public boolean existsById(long id) {
        return usersStorage.containsKey(id);
    }

    public boolean existsByEmail(String email) {
        return usersStorage.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public Optional<User> findByEmail(String email) {
        return usersStorage.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    private long generateId() {
        return ++maxId;
    }

    private void checkEmail(String email) {
        if (existsByEmail(email)) {
            log.warn("Try to add new user with existing email: {}", email);
            throw new AlreadyExistsException("Email already exist: " + email);
        }
    }
}
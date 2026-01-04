package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("dbRepo")
public interface DbUserRepository extends JpaRepository<User, Long>, UserRepository {

    @Override
    default User createUser(User user) {
        return save(user);
    }

    @Override
    default User editUser(User user, long userId) {
        User existing = findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getName() != null && !user.getName().isEmpty()) {
            existing.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existing.setEmail(user.getEmail());
        }

        return save(existing);
    }

    @Override
    default User getUser(long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    default void removeUser(long id) {
        deleteById(id);
    }

    @Override
    default boolean existsById(long id) {
        return findById(id).isPresent();
    }

    @Override
    default boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    Optional<User> findByEmail(String email);
}
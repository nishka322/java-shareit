package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
@Qualifier("dbUserRepository")
public interface DbUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
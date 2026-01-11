package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateAndPersistUser() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john.doe@example.com", createdUser.getEmail());

        User persistedUser = userRepository.findById(createdUser.getId()).orElse(null);
        assertNotNull(persistedUser);
        assertEquals(createdUser.getName(), persistedUser.getName());
        assertEquals(createdUser.getEmail(), persistedUser.getEmail());
    }

    @Test
    void shouldUpdateExistingUser() {
        UserDto initialUser = UserDto.builder()
                .name("Initial Name")
                .email("initial@example.com")
                .build();
        UserDto created = userService.createUser(initialUser);

        UserDto updates = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        UserDto updatedUser = userService.updateUser(created.getId(), updates);

        assertEquals(created.getId(), updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void shouldUpdateOnlyNameWhenEmailNotProvided() {
        UserDto initialUser = UserDto.builder()
                .name("Initial Name")
                .email("initial@example.com")
                .build();
        UserDto created = userService.createUser(initialUser);

        UserDto updates = UserDto.builder()
                .name("Updated Name Only")
                .build();

        UserDto updatedUser = userService.updateUser(created.getId(), updates);

        assertEquals(created.getId(), updatedUser.getId());
        assertEquals("Updated Name Only", updatedUser.getName());
        assertEquals("initial@example.com", updatedUser.getEmail());
    }

    @Test
    void shouldUpdateOnlyEmailWhenNameNotProvided() {
        UserDto initialUser = UserDto.builder()
                .name("Initial Name")
                .email("initial@example.com")
                .build();
        UserDto created = userService.createUser(initialUser);

        UserDto updates = UserDto.builder()
                .email("newemail@example.com")
                .build();

        UserDto updatedUser = userService.updateUser(created.getId(), updates);

        assertEquals(created.getId(), updatedUser.getId());
        assertEquals("Initial Name", updatedUser.getName());
        assertEquals("newemail@example.com", updatedUser.getEmail());
    }

    @Test
    void shouldGetUserById() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        UserDto createdUser = userService.createUser(userDto);

        UserDto foundUser = userService.getUserById(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(createdUser.getName(), foundUser.getName());
        assertEquals(createdUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void shouldGetAllUsers() {
        UserDto user1 = UserDto.builder()
                .name("User 1")
                .email("user1@example.com")
                .build();
        UserDto user2 = UserDto.builder()
                .name("User 2")
                .email("user2@example.com")
                .build();

        userService.createUser(user1);
        userService.createUser(user2);

        List<UserDto> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertTrue(allUsers.size() >= 2);
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    void shouldDeleteUser() {
        UserDto userDto = UserDto.builder()
                .name("To Delete")
                .email("delete@example.com")
                .build();
        UserDto createdUser = userService.createUser(userDto);

        assertTrue(userRepository.findById(createdUser.getId()).isPresent());

        userService.deleteUser(createdUser.getId());

        assertFalse(userRepository.findById(createdUser.getId()).isPresent());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        UserDto user1 = UserDto.builder()
                .name("User 1")
                .email("same@example.com")
                .build();
        userService.createUser(user1);

        UserDto user2 = UserDto.builder()
                .name("User 2")
                .email("same@example.com")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user2));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEmailToExistingEmail() {
        UserDto user1 = UserDto.builder()
                .name("User 1")
                .email("user1@example.com")
                .build();
        UserDto user2 = UserDto.builder()
                .name("User 2")
                .email("user2@example.com")
                .build();

        UserDto createdUser1 = userService.createUser(user1);
        userService.createUser(user2);

        UserDto updates = UserDto.builder()
                .email("user2@example.com")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(createdUser1.getId(), updates));
    }

    @Test
    void shouldUpdateEmailToSameEmail() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        UserDto createdUser = userService.createUser(userDto);

        UserDto updates = UserDto.builder()
                .email("test@example.com")
                .name("Updated Name")
                .build();

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updates);

        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("Updated Name", updatedUser.getName());
    }
}
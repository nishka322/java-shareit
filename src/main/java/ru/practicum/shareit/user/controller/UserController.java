package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Creating new user");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@RequestBody UserDto user, @PathVariable long userId) {
        log.info("Updating user under id {}", userId);
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Getting user by id = {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        log.info("Remove user by id = {}", userId);
        userService.deleteUser(userId);
    }

}
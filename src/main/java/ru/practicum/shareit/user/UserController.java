package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
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
    public User createUser(@Valid @RequestBody User user) {
        log.info("Creating new user");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User editUser(@RequestBody User user, @PathVariable long userId) {
        log.info("Updating user under id {}", userId);
        return userService.editUser(user, userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Getting user by id = {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        log.info("Remove user by id = {}", userId);
        userService.removeUser(userId);
    }

}
package ru.practicum.shareit.user.model;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Valid
public class User {
    private long id;

    private String name;

    private String email;
}
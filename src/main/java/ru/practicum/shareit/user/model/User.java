package ru.practicum.shareit.user.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Valid
public class User {
    private long id;

    @NotNull
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
}
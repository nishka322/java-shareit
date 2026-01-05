package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Valid
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCommentDto {
    @NotBlank
    @Size(min = 1, max = 500, message = "Текст комментария должен быть от 1 до 500 символов")
    private String text;
}
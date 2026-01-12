package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
@Data
public class CommentResponseDto {
    private long id;
    @NotBlank
    private String text;
    @NotBlank
    private String authorName;
    @NotNull
    private LocalDateTime created;
}
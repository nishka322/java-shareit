package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", source = "created")
    Comment mapNewCommentToComment(NewCommentDto dto, User user, Item item, LocalDateTime created);

    @Mapping(target = "authorName", source = "comment.user.name")
    CommentResponseDto mapCommentToResponse(Comment comment);
}
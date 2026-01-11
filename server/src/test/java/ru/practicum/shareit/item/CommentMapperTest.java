package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommentMapperImpl.class})
class CommentMapperTest {

    @Autowired
    private CommentMapper mapper;

    @Test
    void shouldMapNewCommentDtoToComment() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Item item = new Item();
        item.setId(10L);

        NewCommentDto dto = new NewCommentDto("Great item!");
        LocalDateTime created = LocalDateTime.now();

        Comment comment = mapper.mapNewCommentToComment(dto, user, item, created);

        assertNotNull(comment);
        assertEquals("Great item!", comment.getText());
        assertEquals(user, comment.getUser());
        assertEquals(item, comment.getItem());
        assertEquals(created, comment.getCreated());
    }

    @Test
    void shouldMapCommentToCommentResponseDto() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Item item = new Item();
        item.setId(10L);

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Excellent quality!");
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        CommentResponseDto dto = mapper.mapCommentToResponse(comment);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals("Excellent quality!", dto.getText());
        assertEquals("John Doe", dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void shouldHandleNullUserWhenMappingToResponse() {
        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Test comment");
        comment.setUser(null);
        comment.setCreated(LocalDateTime.now());

        CommentResponseDto dto = mapper.mapCommentToResponse(comment);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertNull(dto.getAuthorName());
    }
}
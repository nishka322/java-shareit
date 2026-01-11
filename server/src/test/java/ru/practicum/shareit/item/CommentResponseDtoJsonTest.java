package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseDtoJsonTest {

    @Autowired
    private JacksonTester<CommentResponseDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldSerializeCommentResponseDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John Doe")
                .created(created)
                .build();

        JsonContent<CommentResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2024-01-15T14:30:00");
    }

    @Test
    void shouldDeserializeCommentResponseDto() throws Exception {
        String content = "{\"id\": 1, \"text\": \"Good quality\", \"authorName\": \"Jane Smith\", " +
                "\"created\": \"2024-01-16T10:15:30\"}";

        CommentResponseDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Good quality");
        assertThat(dto.getAuthorName()).isEqualTo("Jane Smith");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 16, 10, 15, 30));
    }

    @Test
    void shouldSerializeWithCurrentDateTime() throws Exception {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(2L)
                .text("Test")
                .authorName("Test User")
                .created(now)
                .build();

        JsonContent<CommentResponseDto> result = json.write(dto);

        String expectedDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .startsWith(expectedDate.substring(0, 16));
    }
}
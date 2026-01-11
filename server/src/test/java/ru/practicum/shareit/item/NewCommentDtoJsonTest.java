package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.comment.NewCommentDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewCommentDtoJsonTest {

    @Autowired
    private JacksonTester<NewCommentDto> json;

    @Test
    void shouldSerializeNewCommentDto() throws Exception {
        NewCommentDto dto = new NewCommentDto("Great item! Works perfectly.");

        JsonContent<NewCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Great item! Works perfectly.");
    }

    @Test
    void shouldDeserializeNewCommentDto() throws Exception {
        String content = "{\"text\":\"Excellent condition, would recommend!\"}";

        NewCommentDto dto = json.parseObject(content);

        assertThat(dto.getText()).isEqualTo("Excellent condition, would recommend!");
    }

    @Test
    void shouldSerializeWithEmptyText() throws Exception {
        NewCommentDto dto = new NewCommentDto("");

        JsonContent<NewCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("");
    }
}
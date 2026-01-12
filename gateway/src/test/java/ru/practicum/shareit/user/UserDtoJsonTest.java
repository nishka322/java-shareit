package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeUserDto() throws Exception {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldDeserializeUserDtoWithoutId() throws Exception {
        String jsonContent = "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldHandleMissingFields() throws Exception {
        String jsonContent = "{\"name\":\"John Doe\"}";

        UserDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isNull();
    }
}
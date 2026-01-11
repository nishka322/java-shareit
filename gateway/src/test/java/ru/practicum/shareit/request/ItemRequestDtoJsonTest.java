package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private JacksonTester<ItemRequestIncomingDto> incomingJson;

    @Test
    void shouldSerializeItemRequestDto() throws Exception {
        ItemRequestDto.ItemResponseDto itemResponse = ItemRequestDto.ItemResponseDto.builder()
                .id(1L)
                .name("Drill")
                .ownerId(2L)
                .description("Powerful drill")
                .available(true)
                .requestId(3L)
                .build();

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill for construction work")
                .created(LocalDateTime.of(2024, 12, 1, 10, 0, 0))
                .items(List.of(itemResponse))
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill for construction work");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2024-12-01T10:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Drill");
    }

    @Test
    void shouldDeserializeItemRequestDto() throws Exception {
        String jsonContent = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2024-12-01T10:00:00\",\"items\":[]}";

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 0, 0));
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void shouldSerializeItemRequestIncomingDto() throws Exception {
        ItemRequestIncomingDto dto = new ItemRequestIncomingDto("Need a drill");

        JsonContent<ItemRequestIncomingDto> result = incomingJson.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill");
    }

    @Test
    void shouldDeserializeItemRequestIncomingDto() throws Exception {
        String jsonContent = "{\"description\":\"Need a drill\"}";

        ItemRequestIncomingDto dto = incomingJson.parse(jsonContent).getObject();

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }
}
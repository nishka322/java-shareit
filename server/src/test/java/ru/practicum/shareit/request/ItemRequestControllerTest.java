package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void shouldCreateRequest() throws Exception {
        ItemRequestIncomingDto incomingDto = new ItemRequestIncomingDto("Need a drill");
        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.createRequest(eq(1L), any(ItemRequestIncomingDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void shouldReturnBadRequestWhenUserIdHeaderMissing() throws Exception {
        ItemRequestIncomingDto incomingDto = new ItemRequestIncomingDto("Need a drill");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOwnRequests() throws Exception {
        ItemRequestDto request1 = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto request2 = ItemRequestDto.builder()
                .id(2L)
                .description("Need a hammer")
                .created(LocalDateTime.now().plusHours(1))
                .build();

        when(itemRequestService.getOwnRequests(1L))
                .thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(3L)
                .description("Other user's request")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.getAllRequests(1L))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .id(1L)
                .description("Specific request")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.getRequestById(1L, 1L))
                .thenReturn(request);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Specific request"));
    }

    @Test
    void shouldReturnNotFoundWhenRequestDoesNotExist() throws Exception {
        when(itemRequestService.getRequestById(1L, 999L))
                .thenThrow(new ru.practicum.shareit.exceptions.NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/999")
                        .header(USER_ID_HEADER, "1"))
                .andExpect(status().isNotFound());
    }
}
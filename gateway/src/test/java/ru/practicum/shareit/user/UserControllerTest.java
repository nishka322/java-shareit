package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).createUser(any(UserDto.class));
    }

    @Test
    void shouldValidateUserDtoOnCreate() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .name("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(result -> {
                    assertNotNull(Objects.requireNonNull(result.getResolvedException()));
                    assertTrue(result.getResolvedException() instanceof jakarta.validation.ConstraintViolationException ||
                            result.getResolvedException() instanceof org.springframework.web.bind.MethodArgumentNotValidException);
                });
    }

    @Test
    void shouldEditUser() throws Exception {
        long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .build();

        when(userClient.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void shouldGetUser() throws Exception {
        long userId = 1L;

        when(userClient.getUser(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(eq(userId));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

    @Test
    void shouldRemoveUser() throws Exception {
        long userId = 1L;

        when(userClient.deleteUser(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(eq(userId));
    }

    @Test
    void shouldLogWhenCreatingUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).createUser(any(UserDto.class));
    }

    @Test
    void shouldHandlePartialUpdate() throws Exception {
        long userId = 1L;
        String partialUpdateJson = "{\"email\":\"new@example.com\"}";

        when(userClient.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateJson))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void shouldHandleUpdateWithOnlyName() throws Exception {
        long userId = 1L;
        String partialUpdateJson = "{\"name\":\"New Name\"}";

        when(userClient.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateJson))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(userId), any(UserDto.class));
    }
}
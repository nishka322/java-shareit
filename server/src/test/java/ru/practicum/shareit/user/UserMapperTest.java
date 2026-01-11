package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void shouldMapUserToDto() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto dto = mapper.toDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john.doe@example.com", dto.getEmail());
    }

    @Test
    void shouldMapDtoToEntity() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        User entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Jane Smith", entity.getName());
        assertEquals("jane.smith@example.com", entity.getEmail());
    }

    @Test
    void shouldMapDtoToEntityWithNullId() {
        UserDto dto = UserDto.builder()
                .name("New User")
                .email("new.user@example.com")
                .build();

        User entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(0L, entity.getId()); // default value for long
        assertEquals("New User", entity.getName());
        assertEquals("new.user@example.com", entity.getEmail());
    }

    @Test
    void shouldUpdateEntityFromDto() {
        User existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UserDto updates = UserDto.builder()
                .name("New Name")
                .email("new@example.com")
                .build();

        User updated = mapper.updateEntityFromDto(updates, existingUser);

        assertSame(existingUser, updated);
        assertEquals(1L, updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void shouldUpdateOnlyNameWhenEmailIsNull() {
        User existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UserDto updates = UserDto.builder()
                .name("New Name")
                .email(null)
                .build();

        User updated = mapper.updateEntityFromDto(updates, existingUser);

        assertEquals("New Name", updated.getName());
        assertEquals("old@example.com", updated.getEmail());
    }

    @Test
    void shouldUpdateOnlyEmailWhenNameIsNull() {
        User existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .build();

        UserDto updates = UserDto.builder()
                .name(null)
                .email("new@example.com")
                .build();

        User updated = mapper.updateEntityFromDto(updates, existingUser);

        assertEquals("Old Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void shouldNotUpdateWhenDtoIsNull() {
        User existingUser = User.builder()
                .id(1L)
                .name("Original Name")
                .email("original@example.com")
                .build();

        User updated = mapper.updateEntityFromDto(null, existingUser);

        assertSame(existingUser, updated);
        assertEquals("Original Name", updated.getName());
        assertEquals("original@example.com", updated.getEmail());
    }

    @Test
    void shouldHandleNullInput() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
    }
}
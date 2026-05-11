package example.user;

import example.exception.ConditionNotMetException;
import example.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository; // создаем mock репозиторий

    @InjectMocks
    private UserServiceImpl userService; // вставляем наш мок в сервис

    @Test
    void getAllUsers_shouldReturnListUserResponse_whenUserExist() {
        // given
        User user1 = new User(1L, "John", "john@mail.com");
        User user2 = new User(2L, "Jane", "jane@mail.com");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users); // получаем фейковые данные из бд

        // when
        List<UserResponse> result = userService.getAllUsers();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() {
        // given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<UserResponse> result = userService.getAllUsers();

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();  // когда вызов всего один раз
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExist() {
        // given
        User user = new User(1L, "John", "jon@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserResponse result = userService.getUserById(1L);

        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John", result.name());
        assertEquals("jon@mail.com", result.email());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
        verify(userRepository).findById(99L);
    }


    @Test
    void createUser_shouldReturnUserResponse() {
        CreateUserRequest request = new CreateUserRequest("Vadim", "vad@mail.com");
        User userToSave = new User(null, "Vadim", "vad@mail.com");
        User savedUser = new User(1L, "Vadim", "vad@mail.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Vadim", response.name());
        assertEquals("vad@mail.com", response.email());

        verify(userRepository).save(argThat(user ->
                user.getId() == null &&
                        user.getName().equals(userToSave.getName()) &&
                        user.getEmail().equals(userToSave.getEmail())

        ));
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("Vadim", "existing@mail.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConditionNotMetException.class, () -> userService.createUser(request));

        // метод не должен быть выполнен, т.к. email уже существует
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldReturnUserResponse_whenValidRequest() {
        // given
        UpdateUserRequest request = new UpdateUserRequest("Updated Name", "update@mail.com");
        User existingUser = new User(1L, "John", "john@mail.com");
        User updatedUser = new User(existingUser.getId(), request.name(), request.email());

        // ищем дубликат email
        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        // получаем в сервисе пользователя
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // сохраняем обновленного
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // when
        UserResponse response = userService.updateUser(1L, request);

        // then
        assertEquals(1L, response.id());
        assertEquals(updatedUser.getName(), response.name());
        assertEquals(updatedUser.getEmail(), response.email());

        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user ->
                user.getId().equals(1L) &&
                        user.getName().equals(updatedUser.getName()) &&
                        user.getEmail().equals(updatedUser.getEmail())

        ));
    }


    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        // given
        UpdateUserRequest request = new UpdateUserRequest("Name", "email@mail.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(99L, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDelete_whenUserExists() {
        //given
        doNothing().when(userRepository).deleteById(1L);

        // when
        userService.deleteUser(1L);

        //then
        verify(userRepository).deleteById(1L);
    }

}
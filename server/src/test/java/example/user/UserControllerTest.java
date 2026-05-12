package example.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.exception.NotFoundException;
import example.exception.handler.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// =========================================== MOCK CONTROLLER LAYER ============================================//
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    // сервис мы сделали mock
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    // mock mvc для имитации запросов к контроллеру
    private MockMvc mvc;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // собираем mvc к нашему контроллеру
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                // для тестирования ошибок validate подключим наш класс с @RestControllerAdvice
                .setControllerAdvice(new ErrorHandler())
                .build();

        userResponse = new UserResponse(
                1L,
                "John",
                "john@mail.com");

    }


    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userResponse));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userResponse.id()))
                .andExpect(jsonPath("$[0].name").value(userResponse.name()))
                .andExpect(jsonPath("$[0].email").value(userResponse.email()));
    }

    @Test
    void create() throws Exception {
        CreateUserRequest request = new CreateUserRequest("John", "john@mail.com");

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(userResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.name())))
                .andExpect(jsonPath("$.email", is(userResponse.email())));

        // тут мы проверяем что сервис был вызван один раз внутри контролера с входящими параметрами
        verify(userService, times(1)).createUser(argThat(user ->
                user.name().equals("John") &&
                        user.email().equals("john@mail.com")));
    }

    @Test
    void getById() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userResponse);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.name())))
                .andExpect(jsonPath("$.email", is(userResponse.email())));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UpdateUserRequest update = new UpdateUserRequest("Vadim", "vad@mail.com");
        UserResponse response = new UserResponse(1L, "Vadim", "vad@mail.com");

        // матчеры смешивать нельзя, либо матчеры все, либо row (1L, update)
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(response);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Vadim"))
                .andExpect(jsonPath("$.email").value("vad@mail.com"));

        verify(userService, times(1))
                .updateUser(eq(1L), any(UpdateUserRequest.class));

    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        // ничего не отдает void
        doNothing().when(userService).deleteUser(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        // выброси исключение
        doThrow(new NotFoundException("User not found"))
                .when(userService).deleteUser(99L);

        mvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
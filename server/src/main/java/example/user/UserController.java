package example.user;

import example.user.dto.CreateUserRequest;
import example.user.dto.UpdateUserRequest;
import example.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers() {
        log.info("GET /users started");
        List<UserResponse> responses = userService.getAllUsers();
        log.info("GET /users completed: count={}", responses.size());
        return responses;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /users started: email={}", request.email());
        UserResponse response = userService.createUser(request);
        log.info("POST /users completed: userId={}", response.id());
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse get(@PathVariable Long id) {
        log.info("GET /users/{} started", id);
        UserResponse response = userService.getUserById(id);
        log.info("GET /users/{} completed", id);
        return response;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateUserRequest request) {
        log.info("PATCH /users/{} started", id);
        UserResponse response = userService.updateUser(id, request);
        log.info("PATCH /users/{} completed", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /users/{} started", id);
        userService.deleteUser(id);
        log.info("DELETE /users/{} completed", id);
    }


}

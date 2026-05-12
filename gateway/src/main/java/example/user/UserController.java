package example.user;


import example.user.dto.CreateUserRequest;
import example.user.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient client;


    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /users on server started: ");
        ResponseEntity<Object> response = client.createUser(request);
        log.info("POST /users on server finished with status: {}", response.getStatusCode());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET /users on server started:");
        ResponseEntity<Object> responses = client.getAllUsers();
        log.info("GET /users on server finished:");
        return responses;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        log.info("GET /users/{} on server started:", id);
        ResponseEntity<Object> response = client.getById(id);
        log.info("GET /users/{} on server finished:", id);
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id,
                                         @RequestBody @Valid UpdateUserRequest request) {
        log.info("PATCH /users/{} on server started:", id);
        ResponseEntity<Object> response = client.updateUser(id, request);
        log.info("PATCH /users/{} on server finished:", id);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("DELETE /users/{} on server started: ", id);
        ResponseEntity<Object> response = client.deleteUser(id);
        log.info("DELETE /users/{} on server finished:", id);
        return response;
    }


}

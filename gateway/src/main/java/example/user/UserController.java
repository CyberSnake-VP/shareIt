package example.user;


import example.client.UserClient;
import example.user.dto.CreateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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






}

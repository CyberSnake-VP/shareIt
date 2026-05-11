package example.user;

import example.exception.ConditionNotMetException;
import example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String NOT_FOUND_USER_MESSAGE = "User not found";
    private static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Email already exists";

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users started:");
        List<UserResponse> responses = userRepository.findAll().stream()
                .map(UserMapper::toUserResponse)
                .toList();
        log.info("Get all users completed. count={}", responses.size());
        return responses;
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Get user by id started: userId={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Get user by id failed. not found userId={}", id);
                    return new NotFoundException(NOT_FOUND_USER_MESSAGE);
                });
        log.info("Get user by id completed: userId={}", id);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Create user started: email={}", request.email());

        boolean isEmailExist = userRepository.existsByEmail(request.email());

        if (isEmailExist) {
            log.warn("Create user failed: email already exists");
            throw new ConditionNotMetException(EMAIL_ALREADY_EXISTS_MESSAGE);
        }

        User user = UserMapper.toUser(request);
        User created = userRepository.save(user);
        log.info("Create user completed: userId={}", created.getId());
        return UserMapper.toUserResponse(created);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Update user started: userId={}", id);

        if (request.hasEmail()) {
            boolean isEmailExist = userRepository.existsByEmail(request.email());
            if (isEmailExist) {
                log.warn("Update user failed: email already exists");
                throw new ConditionNotMetException(EMAIL_ALREADY_EXISTS_MESSAGE);
            }
        }

        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update user failed: not found userId={}", id);
                    return new NotFoundException(NOT_FOUND_USER_MESSAGE);
                });

        User updated = UserMapper.toUser(request, oldUser);
        User savedUser = userRepository.save(updated);

        log.info("Update user completed: userId={}", id);
        return UserMapper.toUserResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Delete user by id started: userId={}", id);
        userRepository.deleteById(id);
        log.info("Delete user by id completed: userId={}", id);
    }
}

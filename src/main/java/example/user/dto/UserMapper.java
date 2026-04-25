package example.user.dto;

import example.user.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        return user;
    }

    public static User toUser(UpdateUserRequest request, User user) {
        if(request.hasEmail()) {
            user.setEmail(request.email());
        }
        if(request.hasName()) {
            user.setName(request.name());
        }
        return user;
    }
}

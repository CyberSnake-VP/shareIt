package example.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserResponse> json;

    @Test
    void testUserResponse() throws Exception {
        UserResponse userResponse = new UserResponse(
                1L,
                "Vadim",
                "vad@mail.com"
        );

        JsonContent<UserResponse> result = json.write(userResponse);


        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Vadim");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("vad@mail.com");
    }
}

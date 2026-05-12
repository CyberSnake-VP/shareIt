package example.client;


import example.user.dto.CreateUserRequest;
import example.user.dto.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class UserClient extends BaseClient{
    private static final String API_PREFIX = "/users";

    // установим через конструктор нашу переменную из профиля(application.yaml)
    // куда пойдут запросы и добавим к ней префикс, создадим RestTemplate через билдер
    public UserClient(@Value("${shareit-server.url}") String url,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                        .requestFactory(()-> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    // создаем чистые методы для нашей задачи, где выбираем подходящий метод из базового репо
    public ResponseEntity<Object> createUser (CreateUserRequest body) {
        return post("",null, body);
    }

    // создаем методы для контроллера
    public ResponseEntity<Object> updateUser(Long userId, UpdateUserRequest body) {
        return patch("/" + userId, body);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }
}

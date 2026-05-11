package example.client;

import jakarta.annotation.Nullable;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }


    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          Long userId,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                // Есть параметры: /users?from=0&size=10
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                // Нет параметров: /users
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

    }


    // подготавливаем заголовок
    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            header.set(USER_ID_HEADER, String.valueOf(userId));
        }
        return header;
    }

}

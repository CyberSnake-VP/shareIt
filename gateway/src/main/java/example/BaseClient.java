package example;

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


    // формируем get запрос с userId, и списком параметров
    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    // два метода get обертки, используем метод get со всем списком возможных параметров.
    // только запрос /requests
    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    // requests и в заголовке userId
    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    // ================================== GET ===================================================//
    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    // путь, заголовок userId, тело запроса
    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return post(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    // =========================================== PUT ======================================== //
    protected <T> ResponseEntity<Object> put(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> put(String path, Long userId, T body) {
        return put(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, T body) {
        return put(path, null, null, body);
    }

    // ========================================= PATCH ======================================== //
    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return patch(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    // ======================================== DELETE ======================================== //
    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }


    // ============================= ОБЩИЙ ГЛАВНЫЙ МЕТОД ДЛЯ ФОРМИРОВАНИЯ И ОТПРАВКИ ЗАПРОСА И ПОЛУЧЕНИЯ РЕЗУЛЬТАТА ====================== //
    // метод для отправки запроса через RestTemplate класс для отправки запросов и обработки результатов spring
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          Long userId,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        // формируем конверт для отправки запроса, где тело запроса и заголовок, тело может быть пустым
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        // возвращаем ответ через ResponseEntity<Object> object означает любой объект ожидаем, класс наш универсальный
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
        return shareitServerResponse;
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

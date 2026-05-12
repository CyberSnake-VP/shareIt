package example.request;

import example.BaseClient;
import example.request.dto.CreateRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    ResponseEntity<Object> create(long requestorId, CreateRequestDto body) {
        return post("", requestorId, body);
    }

    ResponseEntity<Object> getAll() {
        return get("/all");
    }

    ResponseEntity<Object> getById(long requestId) {
        return get("/" + requestId);
    }

    ResponseEntity<Object> getByRequestor(long requestorId) {
        return get("", requestorId);
    }
}

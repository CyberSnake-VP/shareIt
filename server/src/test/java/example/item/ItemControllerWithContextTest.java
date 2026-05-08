package example.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.item.dto.CreateItemRequest;
import example.item.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerWithContextTest {
    private final ObjectMapper mapper;

    @MockBean
    private final ItemService itemService;

    private final MockMvc mvc;

    private ItemResponse response = new ItemResponse(
            1L,
            "Hammer",
            1L,
            "Xuyak",
            true);

    private CreateItemRequest request = new CreateItemRequest(
            "Hammer",
            "Xuyak",
            true);

    @Test
    void create() throws Exception {
        when(itemService.create(any(Long.class), any(CreateItemRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hammer"));
    }
}
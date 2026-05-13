package example.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.exception.ConditionNotMetException;
import example.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ActiveProfiles("test") // включаем тестовый слой с тестовой базой
class ItemControllerWithContextTest {

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final String HEADER_USER = "X-Sharer-User-Id";

    private ItemResponse response = new ItemResponse(
            1L,
            "Hammer",
            1L,
            "Xuyak",
            true);

    private CreateItemRequest request = new CreateItemRequest(
            "Hammer",
            "Xuyak",
            true,
            1L);

    @Test
    void createItem_shouldBeReturnItemResponse() throws Exception {

        when(itemService.create(any(Long.class), any(CreateItemRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hammer"));
    }


    @Test
    void getAll_shouldBeReturnItemCommentResponseList() throws Exception {
        List<ItemCommentResponse> responses = List.of(
                new ItemCommentResponse(
                        1L,
                        "hammer",
                        "beat",
                        true,
                        null,
                        null,
                        null
                ));

        when(itemService.getAll(1L))
                .thenReturn(responses);

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("hammer"))
                .andExpect(jsonPath("$[0].description").value("beat"));

        verify(itemService, times(1)).getAll(1L);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoItems() throws Exception {
        when(itemService.getAll(1L)).thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .header(HEADER_USER, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_shouldReturnItemCommentResponse() throws Exception {
        ItemCommentResponse resp = new ItemCommentResponse(
                1L,
                "hammer",
                "beat",
                true,
                null,
                null,
                null
        );

        when(itemService.getById(1L)).thenReturn(resp);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("hammer"))
                .andExpect(jsonPath("$.description").value("beat"));

        verify(itemService, times(1)).getById(1L);
    }

    @Test
    void getById_shouldReturnNotFound_whenNotExist() throws Exception {
        when(itemService.getById(99L))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_shouldReturnItemResponse() throws Exception {
        UpdateItemRequest update = new UpdateItemRequest("car", "drive", true);

        when(itemService.update(1L, 1L, update))
                .thenReturn(
                        new ItemResponse(
                                1L,
                                "car",
                                1L,
                                "drive",
                                true
                        )
                );

        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("car"))
                .andExpect(jsonPath("$.description").value("drive"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).update(1L, 1L, update);
    }

    @Test
    void updateItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        UpdateItemRequest update = new UpdateItemRequest("car", "drive", true);

        when(itemService.update(99L, 1L, update))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(patch("/items/99")
                        .header(HEADER_USER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_shouldReturnConditionNotMet_whenUserIsNotOwner() throws Exception {
        UpdateItemRequest update = new UpdateItemRequest("car", "drive", true);

        when(itemService.update(1L, 99L, update))
                .thenThrow(new ConditionNotMetException("Not owner"));

        mvc.perform(patch("/items/1")
                        .header(HEADER_USER, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isConflict());
    }

    @Test
    void searchItem_shouldReturnItemResponse() throws Exception {

        when(itemService.search(1L, "text"))
                .thenReturn(List.of(response));

        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Hammer"))
                .andExpect(jsonPath("$[0].description").value("Xuyak"));

        verify(itemService, times(1)).search(1L, "text");
    }

    @Test
    void searchItem_shouldReturnEmptyList_whenItemDoesNotExist() throws Exception {
        when(itemService.search(1L, "text"))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addComment_shouldReturnCommentResponse() throws Exception {
        CreateCommentRequest addComm = new CreateCommentRequest("text");
        CommentResponse commentResponse = new CommentResponse(1L, "text", "Vadim", null);

        when(itemService.addComment(1L, 1L, addComm))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(addComm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.authorName").value("Vadim"));

        verify(itemService, times(1)).addComment(1L, 1L, addComm);
    }

    @Test
    void addComment_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        CreateCommentRequest addComm = new CreateCommentRequest("text");

        when(itemService.addComment(99L, 1L, addComm))
                .thenThrow(new NotFoundException("item not found"));

        mvc.perform(post("/items/99/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER, 1L)
                        .content(mapper.writeValueAsString(addComm)))
                .andExpect(status().isNotFound());

    }

}
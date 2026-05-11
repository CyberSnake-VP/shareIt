package example.item;

import example.item.dto.CreateItemRequest;
import example.item.dto.ItemCommentResponse;
import example.item.dto.ItemResponse;
import example.item.dto.UpdateItemRequest;
import example.user.UserService;
import example.user.CreateUserRequest;
import example.user.UserResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Transactional
@Rollback(value = true) // если нужно оставить сохраненными в бд true
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        CreateUserRequest requestUser = new CreateUserRequest("Vadim", "vad@mail.com");
        UserResponse owner = userService.createUser(requestUser);
        ownerId = owner.id();
    }

    @Test
    void create() {
        CreateItemRequest requestItem = new CreateItemRequest("Hammer", "destroy", true);

        // when
        ItemResponse response = itemService.create(ownerId, requestItem);
        // then
        assertThat(response.id(), notNullValue());
        assertThat(response.name(), equalTo(requestItem.name()));

        // проверяем через бд
        TypedQuery<Item> query = em.createQuery("select i from Item i where owner.id = :id", Item.class);
        Item savedTime = query.setParameter("id", ownerId).getSingleResult();

        assertThat(savedTime.getId(), notNullValue());
        assertThat(savedTime.getName(), equalTo(requestItem.name()));
        assertThat(savedTime.getDescription(), equalTo(requestItem.description()));
        assertThat(savedTime.isAvailable(), equalTo(requestItem.available()));
    }


    @Test
    void getAll() {
        CreateItemRequest requestItem = new CreateItemRequest("Hammer", "destroy", true);
        itemService.create(ownerId, requestItem);

        // when
        List<ItemCommentResponse> result = itemService.getAll(ownerId);


        // then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).name(), equalTo(requestItem.name()));
        assertThat(result.get(0).description(), equalTo(requestItem.description()));
        assertThat(result.get(0).available(), equalTo(requestItem.available()));

        // проверяем через бд
        TypedQuery<Item> query = em.createQuery("select i from Item i where owner.id = :id", Item.class);
        Item item = query.setParameter("id", ownerId).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(requestItem.name(), equalTo(item.getName()));
        assertThat(requestItem.description(), equalTo(item.getDescription()));
        assertThat(requestItem.available(), equalTo(item.isAvailable()));
    }

    @Test
    void getById() {
        CreateItemRequest requestItem = new CreateItemRequest("Hammer", "destroy", true);
        itemService.create(ownerId, requestItem);

        ItemCommentResponse result = itemService.getById(ownerId);

        assertThat(result.id(), notNullValue());
        assertThat(result.name(), equalTo(requestItem.name()));
        assertThat(result.description(), equalTo(requestItem.description()));
        assertThat(result.available(), equalTo(requestItem.available()));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i where i.id = :id", Item.class);
        Item savedItem = query.setParameter("id", result.id()).getSingleResult();

        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), equalTo(requestItem.name()));
        assertThat(savedItem.getDescription(), equalTo(result.description()));
        assertThat(savedItem.isAvailable(), equalTo(result.available()));
    }

    @Test
    void update() {
        CreateItemRequest savedItem = new CreateItemRequest("Hammer", "destroy", true);
        UpdateItemRequest updatedItem = new UpdateItemRequest("Кувалда", "Убивашка", false);

        ItemResponse created = itemService.create(ownerId, savedItem);
        ItemResponse updated = itemService.update(created.id(), ownerId, updatedItem);

        assertThat(updated.id(), notNullValue());
        assertThat(updatedItem.name(), equalTo(updated.name()));
        assertThat(updatedItem.description(), equalTo(updated.description()));
        assertThat(updatedItem.available(), equalTo(updated.available()));
    }
}
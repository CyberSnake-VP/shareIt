package example.item;

import example.booking.BookingService;
import example.booking.dto.BookingResponse;
import example.booking.dto.CreateBookingRequest;
import example.exception.NotFoundException;
import example.user.CreateUserRequest;
import example.user.UserResponse;
import example.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@Rollback(value = true) // если нужно оставить сохраненными в бд true
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    private Long ownerId;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        CreateUserRequest requestUser = new CreateUserRequest("Vadim", "vad@mail.com");
        UserResponse owner = userService.createUser(requestUser);
        ownerId = owner.id();
    }

    @Test
    void create() {
        CreateItemRequest requestItem = new CreateItemRequest("Hammer", "destroy", true, null);

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
        CreateItemRequest requestItem = new CreateItemRequest("Hammer", "destroy", true, null);
        itemService.create(ownerId, requestItem);

        // when
        List<ItemCommentResponse> result = itemService.getAll(ownerId);


        // then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).name(), equalTo(requestItem.name()));
        assertThat(result.get(0).description(), equalTo(requestItem.description()));
        assertThat(result.get(0).available(), equalTo(requestItem.available()));

        // проверяем через бд
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        assertThat(items, hasSize(1));
        Item item = items.get(0);

        assertThat(item.getId(), notNullValue());
        assertThat(requestItem.name(), equalTo(item.getName()));
        assertThat(requestItem.description(), equalTo(item.getDescription()));
        assertThat(requestItem.available(), equalTo(item.isAvailable()));
    }

    @Test
    void getById() {
        CreateItemRequest requestItem = new CreateItemRequest("Hammer", "destroy", true, null);
        ItemResponse created = itemService.create(ownerId, requestItem);

        ItemCommentResponse result = itemService.getById(created.id());

        assertThat(result.id(), notNullValue());
        assertThat(result.name(), equalTo(requestItem.name()));
        assertThat(result.description(), equalTo(requestItem.description()));
        assertThat(result.available(), equalTo(requestItem.available()));

        // проверка через бд, через метод find
        Item savedItm = em.find(Item.class, created.id());
        assertThat(savedItm.getName(), equalTo(requestItem.name()));
    }

    /// Проверяем на выброс исключения при отсутствии item
    @Test
    void getById_shouldThrowNotFound_whenItemDoesNotExist() {
        assertThrows(NotFoundException.class, () -> itemService.getById(999L));
    }

    @Test
    void update() {
        CreateItemRequest savedItem = new CreateItemRequest("Hammer", "destroy", true, null);
        UpdateItemRequest updatedItem = new UpdateItemRequest("Кувалда", "Убивашка", false);

        ItemResponse created = itemService.create(ownerId, savedItem);
        ItemResponse updated = itemService.update(created.id(), ownerId, updatedItem);

        assertThat(updated.id(), notNullValue());
        assertThat(updatedItem.name(), equalTo(updated.name()));
        assertThat(updatedItem.description(), equalTo(updated.description()));
        assertThat(updatedItem.available(), equalTo(updated.available()));

        // проверка через БД, получаем файл через метод find entityManager
        Item dbItem = em.find(Item.class, created.id());
        assertThat(dbItem.getName(), equalTo(updated.name()));
        assertThat(dbItem.getDescription(), equalTo(updated.description()));
        assertThat(dbItem.isAvailable(), equalTo(false));

        // проверка, что старых значений не осталось
        assertThat(dbItem.getName(), not(equalTo(savedItem.name())));
    }

    @Test
    void update_shouldThrowNotFound_whenItemDoesNotExist() {
        UpdateItemRequest update = new UpdateItemRequest("new", "new", true);
        assertThrows(NotFoundException.class, () -> itemService.update(999L, ownerId, update));
    }

    @Test
    void update_shouldThrowNotFound_whenUserIsNotOwner() {
        CreateItemRequest request = new CreateItemRequest("hammer", "destroy", true, null);
        ItemResponse created = itemService.create(ownerId, request);

        UpdateItemRequest update = new UpdateItemRequest("new", "new", true);
        Long wrongUserId = 999L;

        // пользователь не создавал вещь с id 999, поэтому при изменении объекта он не будет найден у данного пользователя
        // then
        assertThrows(NotFoundException.class, () -> itemService.update(created.id(), wrongUserId, update));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoItems() {
        List<ItemCommentResponse> result = itemService.getAll(ownerId);
        assertThat(result, is(empty()));
    }

    @Test
    void search_shouldReturnItemsResponseList() {
        // given
        CreateItemRequest request = new CreateItemRequest("hammer", "destroy", true, null);
        itemService.create(ownerId, request);

        // when
        List<ItemResponse> result = itemService.search(ownerId, "ham");

        // then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).name(), is("hammer"));
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatches() {
        List<ItemResponse> result = itemService.search(ownerId, "none");

        assertThat(result, is(empty()));
    }

    @Test
    void search_shouldReturnMultipleItems() {
        itemService.create(ownerId, new CreateItemRequest("hammer", "destroy", true, null));
        itemService.create(ownerId, new CreateItemRequest("hammer pro", "pro tool", true, null));

        List<ItemResponse> result = itemService.search(ownerId, "ham");

        // анализируем список с помощью матчера hasItem при помощи property
        assertThat(result, hasSize(2));
        assertThat(result.get(0).name(), is("hammer"));
        assertThat(result.get(1).name(), is("hammer pro"));
    }

    @Test
    void addComment_shouldReturnCommentResponse() {
        // создаем пользователей
        CreateUserRequest userRequest = new CreateUserRequest("John", "john@mail.com");
        UserResponse booker = userService.createUser(userRequest); // кто арендует, наш букер

        CreateUserRequest ownerRequest = new CreateUserRequest("Vadim", "vadimka@mail.com");
        UserResponse owner = userService.createUser(ownerRequest); // владелец вещи

        // создаем вещь ее владельцем
        CreateItemRequest itemRequest = new CreateItemRequest("hammer", "destroy", true, null);
        ItemResponse item = itemService.create(owner.id(), itemRequest);

        // создаем бронирование
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        CreateBookingRequest bookingRequest = new CreateBookingRequest(item.id(), start, end);
        BookingResponse booking = bookingService.create(booker.id(), bookingRequest);

        // подтверждаем бронирование владельцем из состояния waiting в approved
        bookingService.updateBookingStatus(booking.id(), owner.id(), true);

        // наконец-то добавляем комментарии
        CreateCommentRequest commentRequest  = new CreateCommentRequest("Great tool!");
        CommentResponse comment = itemService.addComment(item.id(), booker.id(), commentRequest);


        // проверки
        assertThat(comment.id(), is(notNullValue()));
        assertThat(comment.text(), equalTo("Great tool!"));
        assertThat(comment.authorName(), equalTo("John"));

        // проверка записи в бд комментов, указываем entity и ключ primary key
        // проверяем текст, автора комментария и id вещи
        Comment savedComment = em.find(Comment.class, comment.id());
        assertThat(savedComment.getText(), equalTo("Great tool!"));
        assertThat(savedComment.getItem().getId(), equalTo(item.id()));
        assertThat(savedComment.getAuthor().getId(), equalTo(booker.id()));
    }
}
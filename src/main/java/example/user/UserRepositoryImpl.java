package example.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    // Хранение в памяти по ТЗ 14 спринта.
    // Ключ:id Значение:пользователь
    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if(user.getId() == null) {
            user.setId(++idGenerator);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public boolean existEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> Objects.equals(user.getEmail(), email));
    }
}

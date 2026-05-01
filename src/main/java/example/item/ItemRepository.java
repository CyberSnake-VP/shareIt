package example.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    List<Item> findAllByOwnerId(Long ownerId);

}

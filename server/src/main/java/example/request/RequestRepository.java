package example.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {

}

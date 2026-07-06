package huypro.todoappbe.repository;


import huypro.todoappbe.entity.TodoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    Page<TodoEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<TodoEntity> findByCompleted(Boolean completed, Pageable pageable);

    Page<TodoEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<TodoEntity> findByCompletedAndTitleContainingIgnoreCase(
            Boolean completed,
            String title,
            Pageable pageable
    );
}

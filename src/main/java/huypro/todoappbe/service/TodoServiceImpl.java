package huypro.todoappbe.service;


import huypro.todoappbe.common.exception.TodoNotFoundException;
import huypro.todoappbe.common.pagination.PageResponse;
import huypro.todoappbe.dto.request.CreateTodoRequest;
import huypro.todoappbe.dto.request.PatchTodoRequest;
import huypro.todoappbe.dto.request.UpdateTodoRequest;
import huypro.todoappbe.dto.response.TodoResponse;
import huypro.todoappbe.entity.TodoEntity;
import huypro.todoappbe.mapper.TodoMapper;
import huypro.todoappbe.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TodoResponse> findAll(
            int page,
            int size,
            Boolean completed,
            String search
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TodoEntity> result = queryTodos(completed, search, pageable);

        return PageResponse.of(result.map(todoMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse findById(Long id) {
        return todoMapper.toResponse(getTodoEntity(id));
    }

    @Override
    public TodoResponse create(CreateTodoRequest request) {
        TodoEntity entity = todoMapper.toEntity(request);
        return todoMapper.toResponse(todoRepository.save(entity));
    }

    @Override
    public TodoResponse update(Long id, UpdateTodoRequest request) {
        TodoEntity entity = getTodoEntity(id);
        todoMapper.updateEntity(entity, request);
        return todoMapper.toResponse(todoRepository.save(entity));
    }

    @Override
    public TodoResponse patch(Long id, PatchTodoRequest request) {
        TodoEntity entity = getTodoEntity(id);
        boolean changed = todoMapper.applyPatch(entity, request);

        if (!changed) {
            throw new IllegalArgumentException("At least one field must be provided for patch.");
        }

        return todoMapper.toResponse(todoRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        TodoEntity entity = getTodoEntity(id);
        todoRepository.delete(entity);
    }

    private Page<TodoEntity> queryTodos(Boolean completed, String search, Pageable pageable) {
        boolean hasSearch = search != null && !search.isBlank();
        String normalizedSearch = hasSearch ? search.trim() : null;

        if (completed != null && hasSearch) {
            return todoRepository.findByCompletedAndTitleContainingIgnoreCase(
                    completed,
                    normalizedSearch,
                    pageable
            );
        }

        if (completed != null) {
            return todoRepository.findByCompleted(completed, pageable);
        }

        if (hasSearch) {
            return todoRepository.findByTitleContainingIgnoreCase(normalizedSearch, pageable);
        }

        return todoRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    private TodoEntity getTodoEntity(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}

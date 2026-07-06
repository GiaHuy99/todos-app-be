package huypro.todoappbe.mapper;


import huypro.todoappbe.common.util.TextNormalizer;
import huypro.todoappbe.dto.request.CreateTodoRequest;
import huypro.todoappbe.dto.request.PatchTodoRequest;
import huypro.todoappbe.dto.request.UpdateTodoRequest;
import huypro.todoappbe.dto.response.TodoResponse;
import huypro.todoappbe.entity.TodoEntity;
import org.springframework.stereotype.Component;

@Component
public class TodoMapperImpl implements TodoMapper {

    private static final String TITLE_FIELD = "Title";

    private final TextNormalizer textNormalizer;

    public TodoMapperImpl(TextNormalizer textNormalizer) {
        this.textNormalizer = textNormalizer;
    }

    @Override
    public TodoEntity toEntity(CreateTodoRequest request) {
        return TodoEntity.builder()
                .title(textNormalizer.normalizeRequired(request.title(), TITLE_FIELD))
                .description(textNormalizer.normalizeOptional(request.description()))
                .completed(false)
                .build();
    }

    @Override
    public void updateEntity(TodoEntity entity, UpdateTodoRequest request) {
        entity.setTitle(textNormalizer.normalizeRequired(request.title(), TITLE_FIELD));
        entity.setDescription(textNormalizer.normalizeOptional(request.description()));

        if (request.completed() != null) {
            entity.setCompleted(request.completed());
        }
    }

    @Override
    public boolean applyPatch(TodoEntity entity, PatchTodoRequest request) {
        boolean changed = false;

        if (request.title() != null) {
            entity.setTitle(textNormalizer.normalizeRequired(request.title(), TITLE_FIELD));
            changed = true;
        }

        if (request.description() != null) {
            entity.setDescription(textNormalizer.normalizeOptional(request.description()));
            changed = true;
        }

        if (request.completed() != null) {
            entity.setCompleted(request.completed());
            changed = true;
        }

        return changed;
    }

    @Override
    public TodoResponse toResponse(TodoEntity entity) {
        return new TodoResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

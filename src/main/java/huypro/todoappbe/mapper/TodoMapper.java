package huypro.todoappbe.mapper;


import huypro.todoappbe.dto.request.CreateTodoRequest;
import huypro.todoappbe.dto.request.PatchTodoRequest;
import huypro.todoappbe.dto.request.UpdateTodoRequest;
import huypro.todoappbe.dto.response.TodoResponse;
import huypro.todoappbe.entity.TodoEntity;

public interface TodoMapper {

    TodoEntity toEntity(CreateTodoRequest request);

    void updateEntity(TodoEntity entity, UpdateTodoRequest request);

    boolean applyPatch(TodoEntity entity, PatchTodoRequest request);

    TodoResponse toResponse(TodoEntity entity);
}

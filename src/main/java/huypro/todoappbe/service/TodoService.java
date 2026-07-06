package huypro.todoappbe.service;


import huypro.todoappbe.common.pagination.PageResponse;
import huypro.todoappbe.dto.request.CreateTodoRequest;
import huypro.todoappbe.dto.request.PatchTodoRequest;
import huypro.todoappbe.dto.request.UpdateTodoRequest;
import huypro.todoappbe.dto.response.TodoResponse;

public interface TodoService {

    PageResponse<TodoResponse> findAll(int page, int size, Boolean completed, String search);

    TodoResponse findById(Long id);

    TodoResponse create(CreateTodoRequest request);

    TodoResponse update(Long id, UpdateTodoRequest request);

    TodoResponse patch(Long id, PatchTodoRequest request);

    void delete(Long id);
}

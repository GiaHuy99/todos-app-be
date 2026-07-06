package huypro.todoappbe.dto.request;

import jakarta.validation.constraints.Size;

public record PatchTodoRequest(
        @Size(max = 120, message = "Title must be 120 characters or fewer.")
        String title,

        @Size(max = 500, message = "Description must be 500 characters or fewer.")
        String description,

        Boolean completed
) {
}

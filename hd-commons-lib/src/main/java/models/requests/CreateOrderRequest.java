package models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(

        @Schema(description = "Requester ID", example = "672cb80ce81f670c6f9923ca")
        @NotBlank(message = "The requester ID cannot be empty")
        @Size(min = 24, max = 36, message = "The requester ID must contain between 24 and 36 characters")
        String requesterId,

        @Schema(description = "Customer ID", example = "672cb80ce81f670c6f9923ca")
        @NotBlank(message = "The Customer ID cannot be empty")
        @Size(min = 24, max = 36, message = "The Customer ID must contain between 24 and 36 characters")
        String customerId,

        @Schema(description = "Title of order", example = "Fix my computer")
        @NotBlank(message = "The title cannot be empty")
        @Size(min = 3, max = 45, message = "The title must contain between 3 and 45 characters")
        String title,

        @Schema(description = "Description of order", example = "My computer is not turning on")
        @NotBlank(message = "The Description cannot be empty")
        @Size(min = 10, max = 3000, message = "The Description must contain between 10 and 3000 characters")
        String description,

        @Schema(description = "Status of order", example = "Open")
        @NotBlank(message = "The status cannot be empty")
        @Size(min = 4, max = 15, message = "The status must contain between 4 and 15 characters")
        String status
) {
}

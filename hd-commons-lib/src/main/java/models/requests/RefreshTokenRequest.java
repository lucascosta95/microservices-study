package models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        @Size(min = 16, max = 30, message = "Refresh token must be between 16 and 30 characters")
        String refreshToken
) {
}

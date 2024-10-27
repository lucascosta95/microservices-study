package models.responses;

public record AuthenticationResponse(
        String token,
        String type
) {
}

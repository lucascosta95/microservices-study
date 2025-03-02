package models.responses;

public record OrderResponse(
        Long id,
        String requesterId,
        String customerId,
        String title,
        String description,
        String status,
        String createAt,
        String closedAt
) {
}

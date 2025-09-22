package br.com.lucascosta.helpdeskbff.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import models.exceptions.StandardError;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "OrderController", description = "Controller responsible for order operations")
@RequestMapping("/api/orders")
public interface OrderController {

    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @GetMapping()
    ResponseEntity<List<OrderResponse>> getAll();

    @Operation(summary = "Get all orders paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @GetMapping("/page")
    ResponseEntity<Page<OrderResponse>> getAllPaginated(
            @Parameter(description = "Page number", required = true, example = "0")
            @RequestParam(name = "page", defaultValue = "0") final Integer page,

            @Parameter(description = "Lines per page", required = true, example = "12")
            @RequestParam(name = "linesPerPage", defaultValue = "12") final Integer linesPerPage,

            @Parameter(description = "Order direction", required = true, example = "ASC")
            @RequestParam(name = "direction", defaultValue = "ASC") final String direction,

            @Parameter(description = "Order by", required = true, example = "id")
            @RequestParam(name = "orderBy", defaultValue = "id") final String orderBy
    );

    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> getOrderById(
            @NotNull(message = "Order id cannot be null")
            @Parameter(description = "Order id", required = true, example = "1")
            @PathVariable Long id
    );

    @Operation(summary = "Save new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @PostMapping()
    ResponseEntity<Void> save(@Valid @RequestBody final CreateOrderRequest createOrderRequest);

    @Operation(summary = "Update order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @PutMapping("/{id}")
    ResponseEntity<OrderResponse> update(
            @Parameter(description = "Order id", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Update order request", required = true)
            @Valid @RequestBody UpdateOrderRequest updateOrderRequest
    );

    @Operation(summary = "Delete order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @NotNull(message = "Order id cannot be null")
            @Parameter(description = "Order id", required = true, example = "1")
            @PathVariable Long id
    );
}

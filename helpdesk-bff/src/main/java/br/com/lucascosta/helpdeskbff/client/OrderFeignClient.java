package br.com.lucascosta.helpdeskbff.client;

import jakarta.validation.Valid;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service-api", path = "/api/orders")
public interface OrderFeignClient {

    @GetMapping()
    ResponseEntity<List<OrderResponse>> getAll();

    @GetMapping("/page")
    ResponseEntity<Page<OrderResponse>> getAllPaginated(
            @RequestParam(name = "page", defaultValue = "0") final Integer page,
            @RequestParam(name = "linesPerPage", defaultValue = "12") final Integer linesPerPage,
            @RequestParam(name = "direction", defaultValue = "ASC") final String direction,
            @RequestParam(name = "orderBy", defaultValue = "id") final String orderBy
    );

    @GetMapping("/{id}")
    ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id);

    @PostMapping()
    ResponseEntity<Void> save(@Valid @RequestBody final CreateOrderRequest createOrderRequest);

    @PutMapping("/{id}")
    ResponseEntity<OrderResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest updateOrderRequest);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);
}

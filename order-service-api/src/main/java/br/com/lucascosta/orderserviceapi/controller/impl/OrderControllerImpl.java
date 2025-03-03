package br.com.lucascosta.orderserviceapi.controller.impl;

import br.com.lucascosta.orderserviceapi.controller.OrderController;
import br.com.lucascosta.orderserviceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @Override
    public ResponseEntity<Page<OrderResponse>> getAllPaginated(Integer page, Integer linesPerPage, String direction, String orderBy) {
        return ResponseEntity.ok().body(orderService.getAllPaginated(page, linesPerPage, direction, orderBy));
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(Long id) {
        return ResponseEntity.ok().body(orderService.getOrderById(id));
    }

    @Override
    public ResponseEntity<Void> save(CreateOrderRequest createOrderRequest) {
        orderService.save(createOrderRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @Override
    public ResponseEntity<OrderResponse> update(Long id, UpdateOrderRequest updateOrderRequest) {
        return ResponseEntity.ok().body(orderService.update(id, updateOrderRequest));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

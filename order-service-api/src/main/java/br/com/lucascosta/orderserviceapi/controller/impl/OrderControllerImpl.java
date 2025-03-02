package br.com.lucascosta.orderserviceapi.controller.impl;

import br.com.lucascosta.orderserviceapi.controller.OrderController;
import br.com.lucascosta.orderserviceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import models.requests.CreateOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    public ResponseEntity<Void> save(CreateOrderRequest createOrderRequest) {
        orderService.save(createOrderRequest);
        return ResponseEntity.status(CREATED).build();
    }
}

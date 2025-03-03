package br.com.lucascosta.orderserviceapi.service;

import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAll();

    OrderResponse getOrderById(Long orderId);

    void save(CreateOrderRequest createOrderRequest);

    OrderResponse update(final Long id, UpdateOrderRequest updateOrderRequest);

    void delete(final Long id);
}

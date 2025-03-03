package br.com.lucascosta.orderserviceapi.service;

import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;

public interface OrderService {
    OrderResponse getOrderById(Long orderId);

    void save(CreateOrderRequest createOrderRequest);

    OrderResponse update(final Long id, UpdateOrderRequest updateOrderRequest);
}

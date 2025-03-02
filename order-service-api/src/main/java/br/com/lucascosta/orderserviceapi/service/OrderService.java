package br.com.lucascosta.orderserviceapi.service;

import models.requests.CreateOrderRequest;

public interface OrderService {
    void save(CreateOrderRequest createOrderRequest);
}

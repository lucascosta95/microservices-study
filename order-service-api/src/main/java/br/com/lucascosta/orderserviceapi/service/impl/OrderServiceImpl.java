package br.com.lucascosta.orderserviceapi.service.impl;

import br.com.lucascosta.orderserviceapi.mapper.OrderMapper;
import br.com.lucascosta.orderserviceapi.repositories.OrderRepository;
import br.com.lucascosta.orderserviceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import models.requests.CreateOrderRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public void save(CreateOrderRequest createOrderRequest) {
        orderRepository.save(orderMapper.fromRequest(createOrderRequest));
    }
}

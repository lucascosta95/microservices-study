package br.com.lucascosta.orderserviceapi.service.impl;

import br.com.lucascosta.orderserviceapi.entities.Order;
import br.com.lucascosta.orderserviceapi.mapper.OrderMapper;
import br.com.lucascosta.orderserviceapi.repositories.OrderRepository;
import br.com.lucascosta.orderserviceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import models.enums.OrderStatusEnum;
import models.exceptions.ResourceNotFoundException;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public void save(CreateOrderRequest createOrderRequest) {
        orderRepository.save(orderMapper.fromRequest(createOrderRequest));
    }

    @Override
    public OrderResponse update(final Long id, UpdateOrderRequest updateOrderRequest) {
        var entity = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Object not found. Id: %d, Type: %s", id, Order.class.getName()))
        );

        entity = orderMapper.fromRequest(entity, updateOrderRequest);
        if (OrderStatusEnum.CLOSED.name().equals(updateOrderRequest.status())) {
            entity.setClosedAt(LocalDateTime.now());
        }

        return orderMapper.toResponse(orderRepository.save(entity));
    }
}

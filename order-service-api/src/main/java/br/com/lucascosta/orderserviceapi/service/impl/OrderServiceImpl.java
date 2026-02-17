package br.com.lucascosta.orderserviceapi.service.impl;

import br.com.lucascosta.orderserviceapi.clients.UserServiceFeignClient;
import br.com.lucascosta.orderserviceapi.entities.Order;
import br.com.lucascosta.orderserviceapi.mapper.OrderMapper;
import br.com.lucascosta.orderserviceapi.repositories.OrderRepository;
import br.com.lucascosta.orderserviceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.dtos.OrderCreatedMessage;
import models.enums.OrderStatusEnum;
import models.exceptions.ResourceNotFoundException;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import models.responses.UserResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserServiceFeignClient userServiceFeignClient;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final OrderMapper orderMapper;

    @Override
    @Cacheable(value = "orders")
    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponse getOrderById(Long orderId) {
        var entity = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("Object not found. Id: %d, Type: %s", orderId, Order.class.getName())
                        )
                );

        return orderMapper.toResponse(entity);
    }

    @Override
    @Cacheable(value = "orders", key = "#page + '-' + #linesPerPage + '-' + #direction + '-' + #orderBy")
    public Page<OrderResponse> getAllPaginated(Integer page, Integer linesPerPage, String direction, String orderBy) {
        final var pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return orderRepository.findAll(pageRequest)
                .map(orderMapper::toResponse);

    }

    @Override
    @CacheEvict(value = "orders", allEntries = true)
    public void save(CreateOrderRequest createOrderRequest) {
        final var requester = validateUserId(createOrderRequest.requesterId());
        final var customer = validateUserId(createOrderRequest.customerId());
        var entity = orderRepository.save(orderMapper.fromRequest(createOrderRequest));

        log.info("Order created: {}", entity);

        rabbitTemplate.convertAndSend(
                "helpdesk",
                "rk.orders.create",
                new OrderCreatedMessage(orderMapper.toResponse(entity), customer, requester)
        );
    }

    @Override
    @CacheEvict(value = "orders", allEntries = true)
    public OrderResponse update(final Long id, UpdateOrderRequest updateOrderRequest) {
        validateUsers(updateOrderRequest);

        var entity = orderRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("Object not found. Id: %d, Type: %s", id, Order.class.getName())
                        )
                );

        entity = orderMapper.fromRequest(entity, updateOrderRequest);
        if (OrderStatusEnum.CLOSED.name().equals(updateOrderRequest.status())) {
            entity.setClosedAt(LocalDateTime.now());
        }

        return orderMapper.toResponse(orderRepository.save(entity));
    }

    @Override
    @CacheEvict(value = "orders", allEntries = true)
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    UserResponse validateUserId(final String userId) {
        return userServiceFeignClient.findById(userId).getBody();
    }

    void validateUsers(UpdateOrderRequest updateOrderRequest) {
        if (updateOrderRequest.requesterId() != null) validateUserId(updateOrderRequest.requesterId());
        if (updateOrderRequest.customerId() != null) validateUserId(updateOrderRequest.customerId());
    }
}

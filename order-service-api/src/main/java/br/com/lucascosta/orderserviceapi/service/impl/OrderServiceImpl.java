package br.com.lucascosta.orderserviceapi.service.impl;

import br.com.lucascosta.orderserviceapi.clients.UserServiceFeignClient;
import br.com.lucascosta.orderserviceapi.entities.Order;
import br.com.lucascosta.orderserviceapi.mapper.OrderMapper;
import br.com.lucascosta.orderserviceapi.repositories.OrderRepository;
import br.com.lucascosta.orderserviceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.enums.OrderStatusEnum;
import models.exceptions.ResourceNotFoundException;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import models.responses.UserResponse;
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
    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
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
    public Page<OrderResponse> getAllPaginated(Integer page, Integer linesPerPage, String direction, String orderBy) {
        final var pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return orderRepository.findAll(pageRequest)
                .map(orderMapper::toResponse);

    }

    @Override
    public void save(CreateOrderRequest createOrderRequest) {
        final var requester = validateUserId(createOrderRequest.requesterId());
        final var customer = validateUserId(createOrderRequest.customerId());

        //TODO: No futuro será necessario o requester e o customer para enviar e-mail
        log.info("Requester: {}", requester);
        log.info("Customer: {}", customer);

        orderRepository.save(orderMapper.fromRequest(createOrderRequest));
    }

    @Override
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

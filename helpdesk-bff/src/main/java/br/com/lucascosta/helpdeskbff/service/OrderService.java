package br.com.lucascosta.helpdeskbff.service;

import br.com.lucascosta.helpdeskbff.client.OrderFeignClient;
import lombok.RequiredArgsConstructor;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderFeignClient orderFeignClient;

    public List<OrderResponse> getAll() {
        return orderFeignClient.getAll().getBody();
    }

    public OrderResponse getOrderById(Long orderId) {
        return orderFeignClient.getOrderById(orderId).getBody();
    }

    public Page<OrderResponse> getAllPaginated(Integer page, Integer linesPerPage, String direction, String orderBy) {
        return orderFeignClient.getAllPaginated(page, linesPerPage, direction, orderBy).getBody();
    }

    public void save(CreateOrderRequest createOrderRequest) {
        orderFeignClient.save(createOrderRequest);
    }

    public OrderResponse update(final Long id, UpdateOrderRequest updateOrderRequest) {
        return orderFeignClient.update(id, updateOrderRequest).getBody();
    }

    public void delete(Long id) {
        orderFeignClient.delete(id);
    }
}

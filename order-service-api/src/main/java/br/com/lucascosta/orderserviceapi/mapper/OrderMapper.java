package br.com.lucascosta.orderserviceapi.mapper;

import br.com.lucascosta.orderserviceapi.entities.Order;
import models.enums.OrderStatusEnum;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE, nullValueCheckStrategy = ALWAYS)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "createAt", expression = "java(mapCreatedAt())")
    Order fromRequest(CreateOrderRequest createOrderRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "status", source = "updateOrderRequest.status", qualifiedByName = "mapStatus")
    Order fromRequest(@MappingTarget Order entity, UpdateOrderRequest updateOrderRequest);

    OrderResponse toResponse(Order order);

    @Named("mapStatus")
    default OrderStatusEnum mapOrderStatus(String status) {
        return OrderStatusEnum.toEnum(status);
    }

    default LocalDateTime mapCreatedAt() {
        return LocalDateTime.now();
    }
}

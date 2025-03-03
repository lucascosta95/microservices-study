package br.com.lucascosta.orderserviceapi.mapper;

import br.com.lucascosta.orderserviceapi.entities.Order;
import models.enums.OrderStatusEnum;
import models.requests.CreateOrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Named("mapStatus")
    default OrderStatusEnum mapOrderStatus(String status) {
        return OrderStatusEnum.toEnum(status);
    }

    default LocalDateTime mapCreatedAt() {
        return LocalDateTime.now();
    }
}

package com.micropos.order.mapper;

import com.micropos.api.dto.OrderDto;
import com.micropos.api.dto.OrderOutlineDto;
import com.micropos.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    OrderDto toOrderDto(Order order);

    @Mapping(target = "orderId", source = "id")
    OrderOutlineDto toOrderOutlineDto(Order order);

}

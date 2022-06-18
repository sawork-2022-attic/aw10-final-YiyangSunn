package com.micropos.delivery.mapper;

import com.micropos.api.dto.DeliveryDto;
import com.micropos.delivery.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "deliveryId", source = "id")
    DeliveryDto toDeliveryDto(Delivery delivery);

}

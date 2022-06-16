package com.micropos.delivery.mapper;

import com.micropos.api.dto.DeliveryDto;
import com.micropos.delivery.model.Delivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto toDeliveryDto(Delivery delivery);

}

package com.micropos.delivery.mapper;

import com.micropos.api.dto.DeliveryInfoDto;
import com.micropos.delivery.model.DeliveryInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryInfoMapper {

    DeliveryInfo toDeliveryInfo(DeliveryInfoDto deliveryInfoDto);

}

package com.micropos.delivery;

import com.micropos.api.dto.DeliveryInfoDto;
import com.micropos.delivery.mapper.DeliveryInfoMapper;
import com.micropos.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.function.Consumer;

@SpringBootApplication
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryInfoMapper deliveryInfoMapper;

    @Bean
    public Consumer<DeliveryInfoDto> order() {
        return deliveryInfoDto -> deliveryService.createDelivery(deliveryInfoMapper.toDeliveryInfo(deliveryInfoDto));
    }
}

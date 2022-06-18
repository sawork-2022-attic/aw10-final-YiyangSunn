package com.micropos.delivery.controller;

import com.micropos.api.controller.DeliveryApi;
import com.micropos.api.dto.DeliveryDto;
import com.micropos.delivery.mapper.DeliveryMapper;
import com.micropos.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class DeliveryController implements DeliveryApi {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Override
    public Mono<ResponseEntity<DeliveryDto>> getDeliveryByOrderId(String orderId, ServerWebExchange exchange) {
        return deliveryService.findDeliveryByOrderId(orderId).flatMap(optional -> optional.isEmpty() ?
                        Mono.just(ResponseEntity.notFound().build()) :
                        Mono.just(ResponseEntity.ok().body(deliveryMapper.toDeliveryDto(optional.get()))));
    }

}

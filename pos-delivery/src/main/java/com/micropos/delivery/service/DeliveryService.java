package com.micropos.delivery.service;

import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.model.DeliveryInfo;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface DeliveryService {

    void createDelivery(DeliveryInfo deliveryInfo);

    Mono<Optional<Delivery>> findDelivery(String deliveryId);

}

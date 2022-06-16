package com.micropos.delivery.repository;

import com.micropos.delivery.model.Delivery;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface DeliveryRepository {

    Mono<Boolean> saveDelivery(Delivery delivery);

    Mono<Optional<Delivery>> findDeliveryById(String deliveryId);

}

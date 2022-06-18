package com.micropos.delivery.repository;

import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.model.DeliveryPhase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface DeliveryRepository {

    Mono<Boolean> saveDelivery(Delivery delivery);

    Mono<Optional<Delivery>> findDeliveryById(String deliveryId);

    Mono<Optional<Delivery>> findDeliveryByOrderId(String orderId);

    Flux<String> getDeliveryIds();

    Mono<Boolean> addDeliveryPhase(String deliveryId, DeliveryPhase phase);

}

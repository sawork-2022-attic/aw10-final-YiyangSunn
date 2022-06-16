package com.micropos.delivery.repository;

import com.micropos.delivery.model.Delivery;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SimpleDeliveryRepository implements DeliveryRepository {

    private final Map<String, Delivery> table = new ConcurrentHashMap<>();

    @Override
    public Mono<Boolean> saveDelivery(Delivery delivery) {
        table.put(delivery.getDeliveryId(), delivery);
        return Mono.just(true);
    }

    @Override
    public Mono<Optional<Delivery>> findDeliveryById(String deliveryId) {
        Delivery delivery = table.getOrDefault(deliveryId, null);
        return Mono.just(delivery == null ? Optional.empty() : Optional.of(delivery));
    }

}

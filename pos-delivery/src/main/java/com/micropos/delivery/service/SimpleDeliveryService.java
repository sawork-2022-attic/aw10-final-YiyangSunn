package com.micropos.delivery.service;

import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.model.DeliveryInfo;
import com.micropos.delivery.model.DeliveryPhase;
import com.micropos.delivery.repository.DeliveryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class SimpleDeliveryService implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public SimpleDeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public void createDelivery(DeliveryInfo deliveryInfo) {
        log.info("Create new Delivery: " + deliveryInfo);
        Delivery delivery = new Delivery();
        delivery.setOrderId(deliveryInfo.getOrderId());
        delivery.setCarrier(deliveryInfo.getCarrier());
        delivery.setPhases(new ArrayList<>(List.of(initialPhase())));
        Boolean saveOk = deliveryRepository.saveDelivery(delivery).block();
        if (saveOk == null || !saveOk) {
            log.error("Failed to save delivery!");
        }
    }

    @Override
    public Mono<Optional<Delivery>> findDeliveryByOrderId(String orderId) {
        return deliveryRepository.findDeliveryByOrderId(orderId);
    }

    private DeliveryPhase initialPhase() {
        String phaseId = UUID.randomUUID().toString();
        Long timeStamp = System.currentTimeMillis();
        String message = "这是初始物流状态";
        return new DeliveryPhase(phaseId, timeStamp, message);
    }

}

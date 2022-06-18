package com.micropos.delivery.repository;

import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.model.DeliveryPhase;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MongoDeliveryRepository implements DeliveryRepository {

    private static final String COLLECTION = "delivery";

    private final MongoTemplate mongoTemplate;

    public MongoDeliveryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Boolean> saveDelivery(Delivery delivery) {
        return Mono.fromCallable(() -> mongoTemplate.save(delivery, COLLECTION).getId() != null);
    }

    @Override
    public Mono<Optional<Delivery>> findDeliveryById(String deliveryId) {
        return Mono.fromCallable(() -> {
            Delivery delivery = mongoTemplate.findById(deliveryId, Delivery.class, COLLECTION);
            return delivery == null ? Optional.empty() : Optional.of(delivery);
        });
    }

    @Override
    public Mono<Optional<Delivery>> findDeliveryByOrderId(String orderId) {
        return Mono.fromCallable(() -> {
            Query query = new Query().addCriteria(Criteria.where("orderId").is(orderId));
            Delivery delivery = mongoTemplate.findOne(query, Delivery.class, COLLECTION);
            return delivery == null ? Optional.empty() : Optional.of(delivery);
        });
    }

    @Override
    public Flux<String> getDeliveryIds() {
        return Mono.fromCallable(() -> {
            Query query = new Query();
            query.fields().include("_id");
            List<Delivery> list = mongoTemplate.find(query, Delivery.class, COLLECTION);
            return list.stream().map(Delivery::getId).collect(Collectors.toList());
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Boolean> addDeliveryPhase(String deliveryId, DeliveryPhase phase) {
        return Mono.fromCallable(() -> {
            Delivery delivery = mongoTemplate.findById(deliveryId, Delivery.class, COLLECTION);
            if (delivery == null) {
                return false;
            }
            delivery.getPhases().add(phase);
            Query query = new Query().addCriteria(Criteria.where("_id").is(deliveryId));
            Update update = new Update().set("phases", delivery.getPhases());
            UpdateResult result = mongoTemplate.updateFirst(query, update, COLLECTION);
            return result.wasAcknowledged() && result.getMatchedCount() == 1;
        });
    }
}

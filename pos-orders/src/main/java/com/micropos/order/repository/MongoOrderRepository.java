package com.micropos.order.repository;

import com.micropos.order.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Repository
public class MongoOrderRepository implements OrderRepository {

    private static final String COLLECTION = "order";

    private final MongoTemplate mongoTemplate;

    public MongoOrderRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Order> findOrdersByCartId(String cartId) {
        return Mono.fromCallable(() -> {
            Query query = new Query().addCriteria(Criteria.where("cartId").is(cartId));
            return mongoTemplate.find(query, Order.class, COLLECTION);
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Optional<Order>> findOrderById(String orderId) {
        return Mono.fromCallable(() -> {
            Order order = mongoTemplate.findById(orderId, Order.class, COLLECTION);
            return order == null ? Optional.empty() : Optional.of(order);
        });
    }

    @Override
    public Mono<Order> saveOrder(Order order) {
        return Mono.fromCallable(() -> mongoTemplate.save(order, COLLECTION));
    }
}

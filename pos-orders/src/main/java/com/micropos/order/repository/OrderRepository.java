package com.micropos.order.repository;

import com.micropos.order.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface OrderRepository {

    Flux<Order> findOrders();

    Mono<Optional<Order>> findOrderById(String orderId);

    Mono<Order> saveOrder(Order order);

}

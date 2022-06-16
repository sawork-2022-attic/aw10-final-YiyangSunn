package com.micropos.order.repository;

import com.micropos.order.model.Order;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SimpleOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Flux<Order> findOrders() {
        List<Order> orderList = new ArrayList<>(orders.values());
        orderList.sort((o1, o2) -> {
            long dt = o1.getCreatedTime() - o2.getCreatedTime();
            return dt > 0 ? -1 : (dt < 0 ? 1 : 0);
        });
        return Flux.fromIterable(orderList);
    }

    @Override
    public Mono<Optional<Order>> findOrderById(String orderId) {
        Order order = orders.getOrDefault(orderId, null);
        return Mono.just(order == null ? Optional.empty() : Optional.of(order));
    }

    @Override
    public Mono<Boolean> saveOrder(Order order) {
        orders.put(order.getOrderId(), order);
        return Mono.just(true);
    }
}

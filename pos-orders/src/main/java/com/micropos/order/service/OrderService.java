package com.micropos.order.service;

import com.micropos.api.dto.ItemDto;
import com.micropos.order.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Flux<Order> ordersByCartId(String cartId);

    Mono<Optional<Order>> orderById(String orderId);

    Mono<Optional<String>> createOrder(String cartId, double total, List<ItemDto> items);

}

package com.micropos.carts.service;

import com.micropos.carts.model.Item;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface CartService {

    Mono<List<Item>> items(String cartId);

    Mono<Optional<Item>> getItem(String cartId, String productId);

    Mono<Boolean> removeAll(String cartId);

    Mono<Boolean> removeItem(String cartId, String productId);

    Mono<Boolean> updateItem(String cartId, String productId, int quantity);

    Mono<Optional<String>> checkout(String cartId);

}

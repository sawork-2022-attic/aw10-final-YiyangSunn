package com.micropos.carts.service;

import com.micropos.carts.model.Item;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface CartService {

    Mono<List<Item>> items();

    Mono<Optional<Item>> getItem(String productId);

    Mono<Boolean> removeAll();

    Mono<Boolean> removeItem(String productId);

    Mono<Boolean> updateItem(String productId, int quantity);

    Mono<Optional<String>> checkout();

}

package com.micropos.carts.repository;

import com.micropos.api.dto.ProductDto;
import com.micropos.carts.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface CartRepository {

    Mono<List<Item>> allItems();

    Mono<Optional<Item>> findItem(String productId);

    // 成功返回 true，失败返回 false

    Mono<Boolean> deleteItem(String productId);

    Mono<Boolean> deleteAll();

    Mono<Boolean> updateItem(String productId, int newQuantity);

    Mono<Boolean> insertItem(ProductDto productDto, int quantity);

}

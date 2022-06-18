package com.micropos.carts.repository;

import com.micropos.api.dto.ProductDto;
import com.micropos.carts.model.Item;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface CartRepository {

    Mono<List<Item>> allItems(String cartId);

    Mono<Optional<Item>> findItem(String cartId, String productId);

    // 成功返回 true，失败返回 false

    Mono<Boolean> deleteItem(String cartId, String productId);

    Mono<Boolean> deleteAll(String cartId);

    Mono<Boolean> updateItem(String cartId, String productId, int newQuantity);

    Mono<Boolean> insertItem(String cartId, ProductDto productDto, int quantity);

}

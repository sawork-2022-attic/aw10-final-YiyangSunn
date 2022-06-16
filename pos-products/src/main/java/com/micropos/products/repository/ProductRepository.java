package com.micropos.products.repository;

import com.micropos.products.model.Product;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Mono<Optional<Product>> findProduct(String productId);

    Mono<Integer> productsCount();

    Mono<List<Product>> productsInRange(int fromIndex, int count);

}

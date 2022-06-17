package com.micropos.products.service;

import com.micropos.products.model.PageResult;
import com.micropos.products.model.Product;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ProductService {

    Mono<Optional<Product>> getProduct(String id);

    Mono<PageResult> productsInPage(int page, int pageSize, String keyword, String category);

}

package com.micropos.products.service;

import com.micropos.products.model.PageResult;
import com.micropos.products.model.Product;
import com.micropos.products.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class SimpleProductService implements ProductService {

    private final ProductRepository productRepository;

    public SimpleProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 把Mono的cache放在cache里
    @Override
    @Cacheable(value = "getProduct")
    public Mono<Optional<Product>> getProduct(String id) {
        return productRepository.findProduct(id).cache();
    }

    @Override
    @Cacheable("getProductsCount")
    public Mono<Integer> getProductsCount() {
        return productRepository.productsCount().cache();
    }

    @Override
    @Cacheable("productsInPage")
    public Mono<PageResult> productsInPage(int page, int pageSize) {
        // 查询商品总数
        Mono<Integer> totalMono = getProductsCount();
        return totalMono.flatMap(total -> {
            // 计算起始位置
            int fromIndex = (page - 1) * pageSize;
            // 获取商品列表
            return productRepository.productsInRange(fromIndex, pageSize)
                    .flatMap(products -> Mono.just(new PageResult(total, products)));
        }).cache();
    }
}

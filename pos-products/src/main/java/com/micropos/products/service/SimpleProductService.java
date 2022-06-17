package com.micropos.products.service;

import com.micropos.products.model.PageResult;
import com.micropos.products.model.Product;
import com.micropos.products.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class SimpleProductService implements ProductService {

    private final ProductRepository productRepository;

    public SimpleProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Cacheable(value = "getProduct")
    public Mono<Optional<Product>> getProduct(String id) {
        // 异步调用查询数据库，缓存的是Mono的缓存，不会查第二遍
        // 返回Optional是因为Mono不能用null
        return Mono.<Optional<Product>>fromCallable(() -> {
            Product product = productRepository.findProduct(id);
            return product == null ? Optional.empty() : Optional.of(product);
        }).cache();
    }

    @Cacheable("productsCount")
    public int productsCount(String keyword, String category) {
        return productRepository.productsCount(keyword, category);
    }

    @Override
    @Cacheable("productsInPage")
    public Mono<PageResult> productsInPage(int page, int pageSize, String keyword, String category) {
        return Mono.fromCallable(() -> {
            // 查询商品总数
            int count = productsCount(keyword, category);
            // 计算起始位置
            int fromIndex = (page - 1) * pageSize;
            // 获取商品列表
            List<Product> products = productRepository.productsInRange(fromIndex, pageSize, keyword, category);
            return new PageResult(count, products);
        }).cache();
    }
}

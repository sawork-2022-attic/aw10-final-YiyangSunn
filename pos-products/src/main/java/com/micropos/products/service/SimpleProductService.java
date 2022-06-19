package com.micropos.products.service;

import com.micropos.products.model.PageResult;
import com.micropos.products.model.Product;
import com.micropos.products.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SimpleProductService implements ProductService {

    private final ProductRepository productRepository;

    private final CacheManager cacheManager;

    public SimpleProductService(ProductRepository productRepository, CacheManager cacheManager) {
        this.productRepository = productRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public Mono<Optional<Product>> getProduct(String id) {
        // 异步调用查询数据库，缓存的是Mono的缓存，不会查第二遍
        // 返回Optional是因为Mono不能用null
        return Mono.fromCallable(() -> {
            // 首先查找缓存
            Cache productCache = cacheManager.getCache("getProduct");
            if (productCache == null) {
                log.warn("No cache available for getProduct()!");
            } else {
                Product product = productCache.get(id, Product.class);
                if (product != null) {
                    // 缓存有
                    return Optional.of(product);
                }
            }
            // 缓存没有，查找数据库
            Product product = productRepository.findProduct(id);
            // 如果非空则放入缓存（这里有缓存穿透）
            if (product != null) {
                if (productCache != null) {
                    productCache.put(id, product);
                }
                return Optional.of(product);
            }
            return Optional.empty();
        });
    }

    public int productsCount(String keyword, String category) {
        // 键值
        Object key = SimpleKeyGenerator.generateKey(keyword, category);
        Cache countCache = cacheManager.getCache("productsCount");
        if (countCache != null) {
            Integer count = countCache.get(key, Integer.class);
            if (count != null) {
                return count;
            }
        } else {
            log.warn("No available cache for productsCount()!");
        }
        // 查找数据库
        int count = productRepository.productsCount(keyword, category);
        if (countCache != null) {
            // 放入缓存
            countCache.put(key, count);
        }
        return count;
    }

    @Override
    public Mono<PageResult> productsInPage(int page, int pageSize, String keyword, String category) {
        return Mono.fromCallable(() -> {
            // 键值
            Object key = SimpleKeyGenerator.generateKey(page, pageSize, keyword, category);
            Cache pagerCache = cacheManager.getCache("productsInPage");
            if (pagerCache == null) {
                log.warn("No cache available for productsInPage()!");
            } else {
                PageResult result = pagerCache.get(key, PageResult.class);
                if (result != null) {
                    return result;
                }
            }
            // 查询商品总数
            int count = productsCount(keyword, category);
            // 计算起始位置
            int fromIndex = (page - 1) * pageSize;
            // 获取商品列表
            List<Product> products = productRepository.productsInRange(fromIndex, pageSize, keyword, category);
            PageResult result = new PageResult(count, products);
            // 放入缓存
            if (pagerCache != null) {
                pagerCache.put(key, result);
            }
            return result;
        });
    }
}

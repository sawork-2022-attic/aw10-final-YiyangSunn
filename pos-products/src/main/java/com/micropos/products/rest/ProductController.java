package com.micropos.products.rest;

import com.micropos.api.controller.ProductsApi;
import com.micropos.api.dto.PageResultDto;
import com.micropos.api.dto.ProductDto;
import com.micropos.products.mapper.ProductMapper;
import com.micropos.products.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController implements ProductsApi {

    private final ProductMapper productMapper;

    private final ProductService productService;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productMapper = productMapper;
        this.productService = productService;
    }

    @Override
    @CircuitBreaker(name = "product-controller", fallbackMethod = "getProductByIdFallback")
    public Mono<ResponseEntity<ProductDto>> getProductById(String productId, ServerWebExchange exchange) {
        return productService.getProduct(productId).flatMap(optional -> optional.isEmpty() ?
                        Mono.just(ResponseEntity.notFound().build()) :
                        Mono.just(ResponseEntity.ok(productMapper.toProductDto(optional.get()))));
    }

    @Override
    @CircuitBreaker(name = "product-controller", fallbackMethod = "getProductsInPageFallback")
    public Mono<ResponseEntity<PageResultDto>> getProductsInPage(
            Integer page, Integer pageSize, String keyword, String category, ServerWebExchange exchange) {
        return productService.productsInPage(page, pageSize, keyword, category)
                .flatMap(pageResult -> {
                    List<ProductDto> productDtos = new ArrayList<>(productMapper.toProductsDto(pageResult.getProducts()));
                    return Mono.just(ResponseEntity.ok(new PageResultDto().total(pageResult.getTotal()).products(productDtos)));
                });
    }

    private Mono<ResponseEntity<ProductDto>> getProductByIdFallback(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    private Mono<ResponseEntity<PageResultDto>> getProductsInPageFallback(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

}

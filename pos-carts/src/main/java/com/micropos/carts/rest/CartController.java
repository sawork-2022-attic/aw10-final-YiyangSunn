package com.micropos.carts.rest;

import com.micropos.api.controller.CartsApi;
import com.micropos.api.dto.ItemDto;
import com.micropos.carts.mapper.ItemMapper;
import com.micropos.carts.service.CartService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// 和 ProductController 基本一样
@Controller
@RequestMapping("/api")
public class CartController implements CartsApi {

    private final CartService cartService;

    private final ItemMapper itemMapper;

    public CartController(CartService cartService, ItemMapper itemMapper) {
        this.cartService = cartService;
        this.itemMapper = itemMapper;
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "defaultFallback")
    public Mono<ResponseEntity<Void>> emptyItems(ServerWebExchange exchange) {
        return cartService.removeAll().flatMap(removeOk -> removeOk ?
                Mono.just(ResponseEntity.ok().build()) :
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "getItemsFallback")
    public Mono<ResponseEntity<Flux<ItemDto>>> getItems(ServerWebExchange exchange) {
        Mono<List<ItemDto>> itemDtos = cartService.items().map(itemMapper::toItemDtos);
        return Mono.just(ResponseEntity.ok(itemDtos.flatMapMany(Flux::fromIterable)));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "defaultFallback")
    public Mono<ResponseEntity<Void>> removeItemById(String productId, ServerWebExchange exchange) {
        return cartService.removeItem(productId).flatMap(removeOk -> removeOk ?
                Mono.just(ResponseEntity.ok().build()) :
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "getItemByIdFallback")
    public Mono<ResponseEntity<ItemDto>> getItemById(String productId, ServerWebExchange exchange) {
        return cartService.getItem(productId).flatMap(optional -> optional.isEmpty() ?
                Mono.just(ResponseEntity.notFound().build()) :
                Mono.just(ResponseEntity.ok(itemMapper.toItemDto(optional.get()))));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "defaultFallback")
    public Mono<ResponseEntity<Void>> updateItemById(String productId, Integer quantity, ServerWebExchange exchange) {
        return cartService.updateItem(productId, quantity).flatMap(updateOk -> updateOk ?
                Mono.just(ResponseEntity.ok().build()) :
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "checkoutAllFallback")
    public Mono<ResponseEntity<String>> checkoutAll(ServerWebExchange exchange) {
        return cartService.checkout().flatMap(optional -> optional.isEmpty() ?
                Mono.just(ResponseEntity.badRequest().build()) :
                Mono.just(ResponseEntity.ok().body(optional.get())));
    }

    public Mono<ResponseEntity<Void>> defaultFallback(Throwable throwable) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    public Mono<ResponseEntity<Flux<ItemDto>>> getItemsFallback(Throwable throwable) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    public Mono<ResponseEntity<ItemDto>> getItemByIdFallback(Throwable throwable) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    public Mono<ResponseEntity<String>> checkoutAllFallback(Throwable throwable) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

}

package com.micropos.carts.rest;

import com.micropos.api.controller.CartsApi;
import com.micropos.api.dto.ItemDto;
import com.micropos.carts.mapper.ItemMapper;
import com.micropos.carts.service.CartService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// 和 ProductController 基本一样
@Slf4j
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
        return exchange.getSession().flatMap(webSession ->
            cartService.removeAll(webSession.getId()).flatMap(removeOk -> removeOk ?
                    Mono.just(ResponseEntity.ok().build()) :
                    Mono.just(ResponseEntity.badRequest().build()))
        );
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "getItemsFallback")
    public Mono<ResponseEntity<Flux<ItemDto>>> getItems(ServerWebExchange exchange) {
        return exchange.getSession().flatMap(webSession -> {
            Mono<List<ItemDto>> itemDtos = cartService.items(webSession.getId()).map(itemMapper::toItemDtos);
            return Mono.just(ResponseEntity.ok(itemDtos.flatMapMany(Flux::fromIterable)));
        });
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "defaultFallback")
    public Mono<ResponseEntity<Void>> removeItemById(String productId, ServerWebExchange exchange) {
        return exchange.getSession().flatMap(webSession ->
                cartService.removeItem(webSession.getId(), productId).flatMap(removeOk -> removeOk ?
                 Mono.just(ResponseEntity.ok().build()) :
                 Mono.just(ResponseEntity.badRequest().build())));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "getItemByIdFallback")
    public Mono<ResponseEntity<ItemDto>> getItemById(String productId, ServerWebExchange exchange) {
        return exchange.getSession().flatMap(webSession ->
                cartService.getItem(webSession.getId(), productId).flatMap(optional -> optional.isEmpty() ?
                 Mono.just(ResponseEntity.notFound().build()) :
                 Mono.just(ResponseEntity.ok(itemMapper.toItemDto(optional.get())))));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "defaultFallback")
    public Mono<ResponseEntity<Void>> updateItemById(String productId, Integer quantity, ServerWebExchange exchange) {
        return exchange.getSession().flatMap(webSession ->
                cartService.updateItem(webSession.getId(), productId, quantity).flatMap(updateOk -> updateOk ?
                 Mono.just(ResponseEntity.ok().build()) :
                 Mono.just(ResponseEntity.badRequest().build())));
    }

    @Override
    @CircuitBreaker(name = "cart-controller", fallbackMethod = "checkoutAllFallback")
    public Mono<ResponseEntity<String>> checkoutAll(ServerWebExchange exchange) {
        // 用会话ID唯一标识购物车
        return exchange.getSession().flatMap(webSession ->
            cartService.checkout(webSession.getId()).flatMap(optional -> optional.isEmpty() ?
                    Mono.just(ResponseEntity.badRequest().build()) :
                    Mono.just(ResponseEntity.ok().body(optional.get()))));
    }

    private Mono<ResponseEntity<Void>> defaultFallback(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    private Mono<ResponseEntity<Flux<ItemDto>>> getItemsFallback(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    private Mono<ResponseEntity<ItemDto>> getItemByIdFallback(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    private Mono<ResponseEntity<String>> checkoutAllFallback(Throwable throwable) {
        throwable.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

}

package com.micropos.order.controller;

import com.micropos.api.controller.OrdersApi;
import com.micropos.api.dto.OrderDto;
import com.micropos.api.dto.OrderOutlineDto;
import com.micropos.api.dto.PaymentDto;
import com.micropos.order.mapper.OrderMapper;
import com.micropos.order.model.Order;
import com.micropos.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class OrderController implements OrdersApi {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderService orderService;

    @Override
    public Mono<ResponseEntity<OrderDto>> getOrderById(String orderId, ServerWebExchange exchange) {
        return orderService.orderById(orderId).flatMap(optional ->
                optional.isEmpty() ?
                 Mono.just(ResponseEntity.notFound().build()) :
                 Mono.just(ResponseEntity.ok().body(orderMapper.toOrderDto(optional.get()))));
    }

    @Override
    public Mono<ResponseEntity<Flux<OrderOutlineDto>>> getOrders(ServerWebExchange exchange) {
        Flux<Order> orders = orderService.orders();
        return Mono.just(ResponseEntity.ok(orders.map(orderMapper::toOrderOutlineDto)));
    }

    @Override
    public Mono<ResponseEntity<String>> createOrder(Mono<PaymentDto> paymentDtoMono, ServerWebExchange exchange) {
        Mono<String> orderIdMono = paymentDtoMono.flatMap(paymentDto ->
                orderService.createOrder(paymentDto.getTotal(), paymentDto.getItems()));
        return orderIdMono.flatMap(orderId -> orderId == null ?
                Mono.just(ResponseEntity.badRequest().build()) :
                Mono.just(ResponseEntity.ok(orderId)));
    }

}
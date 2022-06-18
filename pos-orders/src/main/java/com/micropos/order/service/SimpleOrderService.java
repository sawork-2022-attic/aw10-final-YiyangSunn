package com.micropos.order.service;

import com.micropos.api.dto.DeliveryInfoDto;
import com.micropos.api.dto.ItemDto;
import com.micropos.order.model.Order;
import com.micropos.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SimpleOrderService implements OrderService {

    private final OrderRepository orderRepository;

    private final StreamBridge streamBridge;

    public SimpleOrderService(OrderRepository orderRepository, StreamBridge streamBridge) {
        this.orderRepository = orderRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public Flux<Order> ordersByCartId(String cartId) {
        return orderRepository.findOrdersByCartId(cartId);
    }

    @Override
    public Mono<Optional<Order>> orderById(String orderId) {
        return orderRepository.findOrderById(orderId);
    }

    @Override
    public Mono<Optional<String>> createOrder(String cartId, double total, List<ItemDto> items) {
        // 创建一份订单
        Order order = new Order();
        order.setCartId(cartId);
        order.setCreatedTime(System.currentTimeMillis());
        order.setPayedTime(System.currentTimeMillis() + 60 * 1000);
        order.setTotal(total);
        order.setItems(items);
        order.setOrderStatus("已支付");
        // 放入数据库
        return orderRepository.saveOrder(order).flatMap(result -> {
            if (result.getId() == null) {
                log.error("创建订单取回的ID为null");
                return Mono.just(Optional.empty());
            }
            // 将物流信息放入消息队列
            DeliveryInfoDto deliveryInfoDto = new DeliveryInfoDto().orderId(result.getId()).carrier("南大快递");
            streamBridge.send("order-out-0", deliveryInfoDto);
            return Mono.just(Optional.of(result.getId()));
        });
    }
}

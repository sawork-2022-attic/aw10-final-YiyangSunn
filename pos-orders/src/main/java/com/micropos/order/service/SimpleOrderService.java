package com.micropos.order.service;

import com.micropos.api.dto.DeliveryInfoDto;
import com.micropos.api.dto.ItemDto;
import com.micropos.order.model.Order;
import com.micropos.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleOrderService implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StreamBridge streamBridge;

    @Override
    public Flux<Order> orders() {
        return orderRepository.findOrders();
    }

    @Override
    public Mono<Optional<Order>> orderById(String orderId) {
        return orderRepository.findOrderById(orderId);
    }

    @Override
    public Mono<String> createOrder(double total, List<ItemDto> items) {
        // 创建一份订单
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCreatedTime(System.currentTimeMillis());
        order.setPayedTime(System.currentTimeMillis() + 60 * 1000);
        order.setTotal(total);
        order.setItems(items);
        order.setOrderStatus("已支付");
        // 生成物流编号
        order.setDeliveryId(UUID.randomUUID().toString());
        // 放入数据库
        return orderRepository.saveOrder(order).flatMap(ok -> {
            if (ok) {
                // 将物流信息放入消息队列
                DeliveryInfoDto deliveryInfoDto = new DeliveryInfoDto()
                        .orderId(order.getOrderId())
                        .deliveryId(order.getDeliveryId())
                        .carrier("南大快递");
                streamBridge.send("order-out-0", deliveryInfoDto);
                return Mono.just(order.getOrderId());
            }
            return Mono.empty();
        });
    }
}

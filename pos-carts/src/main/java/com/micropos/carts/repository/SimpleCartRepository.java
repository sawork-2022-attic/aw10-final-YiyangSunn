package com.micropos.carts.repository;

import com.micropos.api.dto.ProductDto;
import com.micropos.carts.model.Item;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SimpleCartRepository implements CartRepository {

    // 防止有线程问题
    private final Map<String, Item> cartTable = new ConcurrentHashMap<>();

    @Override
    public Mono<List<Item>> allItems() {
        return Mono.fromCallable(() -> {
            List<Item> itemList = new ArrayList<>(cartTable.values());
            // 按加入购物车的时间进行排序
            itemList.sort((item1, item2) -> {
                long tsDiff = item1.getTimeStamp() - item2.getTimeStamp();
                return tsDiff < 0 ? -1 : tsDiff > 0 ? 1 : 0;
            });
            return itemList;
        });
    }

    @Override
    public Mono<Optional<Item>> findItem(String productId) {
        Item item = cartTable.getOrDefault(productId, null);
        return Mono.just(item == null ? Optional.empty() : Optional.of(item));
    }

    @Override
    public Mono<Boolean> deleteItem(String productId) {
        if (cartTable.containsKey(productId)) {
            cartTable.remove(productId);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    @Override
    public Mono<Boolean> deleteAll() {
        cartTable.clear();
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> updateItem(String productId, int newQuantity) {
        if (cartTable.containsKey(productId)) {
            Item item = cartTable.get(productId);
            // 更新数量值
            item.setQuantity(newQuantity);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    @Override
    public Mono<Boolean> insertItem(ProductDto productDto, int quantity) {
        // 添加数量必须为正
        if (quantity <= 0) {
            return Mono.just(false);
        }
        // 购物车中应该没有该商品
        if (cartTable.containsKey(productDto.getId())) {
            return Mono.just(false);
        }
        cartTable.put(productDto.getId(), new Item(productDto, quantity, System.currentTimeMillis()));
        return Mono.just(true);
    }

}

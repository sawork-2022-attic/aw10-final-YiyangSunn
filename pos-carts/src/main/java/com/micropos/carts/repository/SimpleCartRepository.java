package com.micropos.carts.repository;

import com.micropos.api.dto.ProductDto;
import com.micropos.carts.model.Cart;
import com.micropos.carts.model.Item;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class SimpleCartRepository implements CartRepository {

    private static final String COLLECTION = "cart";

    @Autowired
    MongoTemplate mongoTemplate;

    private Mono<Cart> getCart(String cartId) {
        return Mono.fromCallable(() -> {
            Query query = new Query().addCriteria(Criteria.where("cartId").is(cartId));
            List<Cart> cartList = mongoTemplate.find(query, Cart.class, COLLECTION);
            if (cartList.isEmpty()) {
                return mongoTemplate.save(new Cart(cartId, new HashMap<>()), COLLECTION);
            }
            return cartList.get(0);
        });
    }

    @Override
    public Mono<List<Item>> allItems(String cartId) {
        return getCart(cartId).map(cart -> {
            List<Item> itemList = new ArrayList<>(cart.getItemMap().values());
            // 按加入购物车的时间进行排序
            itemList.sort((item1, item2) -> {
                long tsDiff = item1.getTimeStamp() - item2.getTimeStamp();
                return tsDiff < 0 ? -1 : tsDiff > 0 ? 1 : 0;
            });
            return itemList;
        });
    }

    @Override
    public Mono<Optional<Item>> findItem(String cartId, String productId) {
        return getCart(cartId).map(cart -> {
            Item item = cart.getItemMap().getOrDefault(productId, null);
            return item == null ? Optional.empty() : Optional.of(item);
        });
    }

    @Override
    public Mono<Boolean> deleteItem(String cartId, String productId) {
        return getCart(cartId).map(cart -> {
            Map<String, Item> itemMap = cart.getItemMap();
            if (itemMap.containsKey(productId)) {
                itemMap.remove(productId);
                Query query = new Query().addCriteria(Criteria.where("cartId").is(cartId));
                Update update = new Update().set("itemMap", itemMap);
                UpdateResult result = mongoTemplate.updateFirst(query, update, COLLECTION);
                return result.wasAcknowledged() && result.getMatchedCount() == 1;
            }
            return false;
        });
    }

    @Override
    public Mono<Boolean> deleteAll(String cartId) {
        return getCart(cartId).map(cart -> {
            Query query = new Query().addCriteria(Criteria.where("cartId").is(cartId));
            Update update = new Update().set("itemMap", new HashMap<String, Item>());
            UpdateResult result = mongoTemplate.updateFirst(query, update, COLLECTION);
            return result.wasAcknowledged() && result.getMatchedCount() == 1;
        });
    }

    @Override
    public Mono<Boolean> updateItem(String cartId, String productId, int newQuantity) {
        return getCart(cartId).map(cart -> {
            Map<String, Item> itemMap = cart.getItemMap();
            if (itemMap.containsKey(productId)) {
                Item item = itemMap.get(productId);
                // 更新数量值
                item.setQuantity(newQuantity);
                Query query = new Query().addCriteria(Criteria.where("cartId").is(cartId));
                Update update = new Update().set("itemMap", itemMap);
                UpdateResult result = mongoTemplate.updateFirst(query, update, COLLECTION);
                return result.wasAcknowledged() && result.getMatchedCount() == 1;
            }
            return false;
        });
    }

    @Override
    public Mono<Boolean> insertItem(String cartId, ProductDto productDto, int quantity) {
        // 添加数量必须为正
        if (quantity <= 0) {
            return Mono.just(false);
        }
        // 购物车中应该没有该商品
        return getCart(cartId).map(cart -> {
            Map<String, Item> itemMap = cart.getItemMap();
            if (itemMap.containsKey(productDto.getId())) {
                return false;
            }
            itemMap.put(productDto.getId(), new Item(productDto, quantity, System.currentTimeMillis()));
            Query query = new Query().addCriteria(Criteria.where("cartId").is(cartId));
            Update update = new Update().set("itemMap", itemMap);
            UpdateResult result = mongoTemplate.updateFirst(query, update, COLLECTION);
            return result.wasAcknowledged() && result.getMatchedCount() == 1;
        });
    }

}

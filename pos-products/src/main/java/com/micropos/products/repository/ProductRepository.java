package com.micropos.products.repository;

import com.micropos.products.model.Product;

import java.util.List;

public interface ProductRepository {

    Product findProduct(String productId);

    int productsCount(String keyword, String category);

    List<Product> productsInRange(int fromIndex, int count, String keyword, String category);

}

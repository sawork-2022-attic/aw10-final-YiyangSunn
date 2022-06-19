package com.micropos.products.repository;

import com.micropos.products.model.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AmazonRepository extends ProductRepository {

    List<String> productIdsInRange(int fromIndex, int count, String keyword, String category);

    List<Product> productsOfIds(List<String> productIds);

    default List<Product> productsInRange(int fromIndex, int count, String keyword, String category) {
        return productsOfIds(productIdsInRange(fromIndex, count, keyword, category));
    }

}

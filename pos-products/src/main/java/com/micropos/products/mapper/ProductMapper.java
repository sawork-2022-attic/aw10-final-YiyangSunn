package com.micropos.products.mapper;

import com.micropos.api.dto.ProductDto;
import com.micropos.products.model.Product;
import org.mapstruct.Mapper;

import java.util.Collection;

// 按照 spring 的方式生成实现类（会自动加上 @Component）
@Mapper(componentModel = "spring")
public interface ProductMapper {

    Collection<ProductDto> toProductsDto(Collection<Product> products);

    ProductDto toProductDto(Product pet);

}

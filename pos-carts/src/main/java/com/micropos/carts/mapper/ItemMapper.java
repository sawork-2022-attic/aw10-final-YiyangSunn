package com.micropos.carts.mapper;

import com.micropos.api.dto.ItemDto;
import com.micropos.carts.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    List<ItemDto> toItemDtos(List<Item> items);

    @Mappings({
            @Mapping(target = "id", expression = "java(item.getProductDto().getId())"),
            @Mapping(target = "name", expression = "java(item.getProductDto().getName())"),
            @Mapping(target = "price", expression = "java(item.getProductDto().getPrice())"),
            @Mapping(target = "image", expression = "java(item.getProductDto().getImage())"),
    })
    ItemDto toItemDto(Item item);
}

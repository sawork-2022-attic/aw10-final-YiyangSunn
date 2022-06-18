package com.micropos.order.model;

import com.micropos.api.dto.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String id;

    private String cartId;

    private Long createdTime;

    private Long payedTime;

    private Double total;

    private String orderStatus;

    private List<ItemDto> items;

}

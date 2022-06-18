package com.micropos.carts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private String cartId;

    private Map<String, Item> itemMap;

}

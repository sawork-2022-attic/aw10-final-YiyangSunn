package com.micropos.carts.model;

import com.micropos.api.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private ProductDto productDto;

    private int quantity;

    private long timeStamp;
}

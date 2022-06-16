package com.micropos.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    private String orderId;

    private String deliveryId;

    private String carrier;

    private List<DeliveryPhase> phases;
}

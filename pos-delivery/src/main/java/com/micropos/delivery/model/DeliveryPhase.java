package com.micropos.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPhase {

    private String phaseId;

    private Long timeStamp;

    private String message;
}

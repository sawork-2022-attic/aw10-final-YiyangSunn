package com.micropos.counter.controller;

import com.micropos.api.controller.CounterApi;
import com.micropos.api.dto.ItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class CounterController implements CounterApi {

    @Override
    public Mono<ResponseEntity<Double>> checkout(Flux<ItemDto> itemDtoList, ServerWebExchange exchange) {
        Mono<Double> totalMono = itemDtoList.reduce(0.0, (total, itemDto) ->
                total + itemDto.getQuantity() * itemDto.getPrice());
        return totalMono.flatMap(total -> Mono.just(ResponseEntity.ok(total)));
    }

}

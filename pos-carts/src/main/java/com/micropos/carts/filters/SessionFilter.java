package com.micropos.carts.filters;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SessionFilter implements WebFilter {

    // webflux不会自动创建session
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getSession().flatMap(webSession -> {
            if (!webSession.isStarted()) {
                webSession.start();
            }
            return chain.filter(exchange);
        });
    }
}

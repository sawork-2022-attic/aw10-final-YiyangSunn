package com.micropos.gateway.integration;

import com.micropos.api.dto.DeliveryDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.messaging.MessageChannel;

@Configuration
public class DeliveryServiceIntegration {

    @Bean
    public MessageChannel deliveryChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inGate() {
        return IntegrationFlows
                .from(WebFlux
                        .inboundGateway("/api/delivery/{deliveryId}")
                        .requestMapping(r -> r.methods(HttpMethod.GET))
                        .payloadExpression("#pathVariables.deliveryId"))
                .channel(deliveryChannel())
                .get();
    }

    @Bean
    public IntegrationFlow outGate() {
        return IntegrationFlows
                .from(deliveryChannel())
                .handle(WebFlux
                        .outboundGateway(m -> "http://localhost:12001/api/delivery/" + m.getPayload())
                        .httpMethod(HttpMethod.GET)
                        .expectedResponseType(DeliveryDto.class)
                )
                .get();
    }

}

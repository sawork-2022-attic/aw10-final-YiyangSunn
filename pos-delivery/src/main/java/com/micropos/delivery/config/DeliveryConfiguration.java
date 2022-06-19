package com.micropos.delivery.config;

import com.micropos.api.dto.DeliveryInfoDto;
import com.micropos.delivery.mapper.DeliveryInfoMapper;
import com.micropos.delivery.model.Delivery;
import com.micropos.delivery.model.DeliveryPhase;
import com.micropos.delivery.repository.DeliveryRepository;
import com.micropos.delivery.service.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class DeliveryConfiguration {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryInfoMapper deliveryInfoMapper;

    @Bean
    public Consumer<DeliveryInfoDto> order() {
        return deliveryInfoDto -> deliveryService.createDelivery(deliveryInfoMapper.toDeliveryInfo(deliveryInfoDto));
    }

    @Bean
    public Supplier<String> phaseSupplier() {
        // 秘密消息
        return () -> "do add phase";
    }

    @Bean
    public Consumer<String> phaseConsumer() {
        return (secret) -> {
//            log.info("Receive secret: " + secret);
            if (secret.equals("do add phase")) {
                // 为每个物流对象添加一条新状态，至多7条
                deliveryRepository.getDeliveryIds().subscribe(deliveryId ->
                    deliveryRepository.findDeliveryById(deliveryId).subscribe(optional -> {
                        if (optional.isEmpty()) {
                            log.error("无法根据ID查找物流信息");
                            return;
                        }
                        Delivery delivery = optional.get();
                        int phaseCount = delivery.getPhases().size();
                        if (phaseCount < 7) {
                            String phaseId = UUID.randomUUID().toString();
                            Long timeStamp = System.currentTimeMillis();
                            String message = phaseCount < 6 ? "这是一条新的物流信息" : "这是最后一条物流信息";
                            DeliveryPhase phase = new DeliveryPhase(phaseId, timeStamp, message);
                            deliveryRepository.addDeliveryPhase(deliveryId, phase).subscribe(addOk -> {
                                if (!addOk) {
                                    log.error("添加物流信息失败");
                                }
                            });
                        }
                }));
            }
        };
    }

}

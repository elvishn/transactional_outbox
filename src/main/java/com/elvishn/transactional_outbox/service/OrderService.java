package com.elvishn.transactional_outbox.service;


import com.elvishn.transactional_outbox.dto.OrderDto;
import com.elvishn.transactional_outbox.entity.OrderEntity;
import com.elvishn.transactional_outbox.repository.OrderRepository;
import com.elvishn.transactional_outbox.service.event.OrderCreatedEvent;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
public class OrderService {

    private final KafkaTemplate<UUID, OrderCreatedEvent> kafkaTemplate;
    private final OrderRepository repo;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public OrderService(OrderRepository repo, KafkaTemplate kafka) {
        this.repo = repo;
        this.kafkaTemplate = kafka;
    }

    public void createOrder(OrderDto order) {
        OrderEntity entity = OrderEntity.builder()
                .name(order.getName())
                .build();
        repo.save(entity);
        OrderCreatedEvent event = new OrderCreatedEvent(entity.getId(), entity.getName());
        CompletableFuture<SendResult<UUID, OrderCreatedEvent>> future = kafkaTemplate
                .send("order-created-events-topic", entity.getId(), event);
        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send message: {}", exception.getMessage());
            } else {
                LOGGER.info("Message send successfully: {}", result.getRecordMetadata());
            }
        });
    }

}

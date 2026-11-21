package com.elvishn.transactional_outbox;

import com.elvishn.transactional_outbox.dto.OrderDto;
import com.elvishn.transactional_outbox.repository.OrderRepository;
import com.elvishn.transactional_outbox.service.OrderService;
import com.elvishn.transactional_outbox.service.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
public class KafkaFailedTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @MockitoBean(name = "kafkaTemplate")
    @Qualifier("kafkaTemplate")
    KafkaTemplate<UUID, OrderCreatedEvent> kafkaTemplate;

    @Autowired
    OrderService orderService;   // твой сервис с createOrder
    @Autowired
    OrderRepository repo;

    @Test
    void orderIsSavedButEventIsLost_whenKafkaSendFails() {
        when(kafkaTemplate.send(anyString(), any(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka down")));

        orderService.createOrder(new OrderDto("Test"));

        assertThat(repo.findAll()).hasSize(1);                  // заказ в БД есть
        verify(kafkaTemplate, times(1)).send(anyString(), any(), any()); // попытка одна, повтора нет
    }
}
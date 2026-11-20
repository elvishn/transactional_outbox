package com.elvishn.transactional_outbox.service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent {
    private UUID orderId;
    private String name;
}

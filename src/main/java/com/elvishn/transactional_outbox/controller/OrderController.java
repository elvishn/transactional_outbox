package com.elvishn.transactional_outbox.controller;

import com.elvishn.transactional_outbox.dto.OrderDto;
import com.elvishn.transactional_outbox.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping(value = "/orders")
    public ResponseEntity<?> create(@RequestBody OrderDto order) {
        service.createOrder(order);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}

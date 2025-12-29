package com.example.controller;


import com.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping(value = "api/order")
@RestController
public class OrderController {

    private final OrderService orderService;


}

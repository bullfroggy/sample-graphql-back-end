package com.example.demo.controllers;

import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

@Controller
public class ClockController {

    @SubscriptionMapping
    public Flux<String> clock() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> LocalTime.now().toString());
    }
}
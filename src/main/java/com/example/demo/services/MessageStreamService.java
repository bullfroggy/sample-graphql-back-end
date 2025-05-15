package com.example.demo.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class MessageStreamService {

    private final Sinks.Many<Object> sink = Sinks.many().replay().latest();

    void constructor() {
        System.out.println(">>> MessageStreamService initialized: " + this);
    }

    public void emitUpdate() {
        sink.tryEmitNext(new Object()); // we don’t need the data, just the signal
    }

    public Flux<Object> getUpdates() {
        return sink.asFlux();
    }
}
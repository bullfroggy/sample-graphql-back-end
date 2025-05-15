package com.example.demo.controllers;

import com.example.demo.entities.Message;
import com.example.demo.repository.MessageRepository;
import com.example.demo.services.MessageStreamService;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.*;

import java.util.List;

@Controller
public class MessageController {

    private final MessageRepository repo;
    private final MessageStreamService stream;

    public MessageController(MessageRepository repo, MessageStreamService stream) {
        this.repo = repo;
        this.stream = stream;
    }

    @QueryMapping
    public Flux<Message> messages() {
        return repo.findAll();
    }

    @MutationMapping
    public Mono<Message> sendMessage(@Argument String content, @Argument String name) {
        return repo.save(new Message(null, content, name))
            .doOnNext(saved -> {
                System.out.println(">>> Mutation received: " + saved);
                stream.emitUpdate();
            });
    }

    @MutationMapping
    public Mono<Message> updateMessage(@Argument Long id, @Argument String content) {
        System.out.println(">>> Attempting to update message");
        return repo.findById(id)
            .doOnNext(existing -> System.out.println(">>> Found: " + existing))
            .flatMap(existing -> {
                existing.setContent(content);
                return repo.save(existing);
            })
            .doOnNext(updated -> {
                System.out.println(">>> Updated: " + updated);
                stream.emitUpdate();
            });
    }

    @MutationMapping
    public Mono<Boolean> deleteMessage(@Argument Long id) {
        return repo.existsById(id)
            .flatMap(exists -> {
                if (!exists) return Mono.just(false);
                return repo.deleteById(id)
                        .then(Mono.fromRunnable(stream::emitUpdate))
                        .thenReturn(true);
            });
    }

    @SubscriptionMapping
    public Flux<List<Message>> messageStream() {
        System.out.println(">>> Subscription connected");
        return stream.getUpdates()
            .flatMap(signal -> {
                System.out.println(">>> Signal received: " + signal);
                return repo.findAll().collectList();
            });
    }
}
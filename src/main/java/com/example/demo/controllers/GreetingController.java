package com.example.demo.controllers;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @QueryMapping
    public String hello() {
        return "Hello from GraphQL!";
    }

    @QueryMapping
    public String goodBye() {
        return "Goodbye from GraphQL!";
    }
}
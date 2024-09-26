package com.example.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/msmbers/sign-in")
    public String sigin() {
        return "members/sign-in";
    }
}

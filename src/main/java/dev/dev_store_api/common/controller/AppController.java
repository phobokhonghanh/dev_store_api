package dev.dev_store_api.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AppController {
    public AppController() {
    }

    @GetMapping("")
    public ResponseEntity<?> app() {
        return ResponseEntity.ok("Hello World");
    }
}

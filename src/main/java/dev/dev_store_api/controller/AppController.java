package dev.dev_store_api.controller;

import dev.dev_store_api.libs.utils.exception.BaseException;
import dev.dev_store_api.libs.utils.exception.NotFoundException;
import dev.dev_store_api.model.dto.AccountResponseDTO;
import dev.dev_store_api.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AppController {
    public AppController(AccountService accountService){
    }
    @GetMapping("")
    public ResponseEntity<?> app() {
        return ResponseEntity.ok("Hello World");
    }
}

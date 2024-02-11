package dev.czimg.aicustomersupportdemo.backend.controller;

import dev.czimg.aicustomersupportdemo.backend.service.CustomerSupportAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class CustomerSupportAgentController {

    @Autowired
    private CustomerSupportAgentService customerSupportAgentService;

    @PostMapping("/message")
    public ResponseEntity<String> generateAgentResponse(@RequestBody String userMessage) {

        Optional<String> aiMessage = Optional.of(customerSupportAgentService.generateAgentResponse(userMessage));

        return ResponseEntity.ok(aiMessage.get());
    }
}

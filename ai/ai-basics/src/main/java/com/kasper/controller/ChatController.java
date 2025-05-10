package com.kasper.controller;

import com.kasper.model.ApiResponse;
import com.kasper.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

//    @GetMapping("/")
//    public String index() {
//        return "redirect:/index.html";
//    }

    @GetMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<ApiResponse<Flux<String>>> chat(@RequestParam(defaultValue = "Tell me a joke") String message) {
        try {
            Flux<String> response = chatService.streamChat(message);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Chat response generated successfully", response));
        } catch (Exception e) {
            logger.error("Error in chat endpoint", e);
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
    
    @GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam(defaultValue = "Tell me a joke") String message) {
        logger.info("Stream chat request received: {}", message);
        
        // Generate a unique conversation ID for this exchange
        final String conversationId = UUID.randomUUID().toString();
        final AtomicLong sequenceCounter = new AtomicLong(0);
        
        return chatService.streamChat(message)
                .onErrorResume(e -> {
                    logger.error("Error in stream processing", e);
                    return Flux.just("Error: " + e.getMessage());
                })
                .map(chunk -> {
                    // Encode whitespace characters for proper frontend rendering
                    // Replace newlines with specific markers that can be decoded in frontend
                    String encodedChunk = chunk.replace("\n", "\\n")
                                              .replace("\t", "\\t")
                                              .replace(" ", " \\s"); // Double spaces

                    System.out.println("Encoded chunk: " + encodedChunk);
                    
                    long sequence = sequenceCounter.incrementAndGet();
                    return ServerSentEvent.<String>builder()
                            .id(conversationId + "-" + sequence)
                            .data(encodedChunk)
                            .build();
                });
    }
} 
package com.kasper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final OpenAiChatModel chatModel;

    public ChatService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public Flux<String> streamChat(String message) {
        if (message == null || message.trim().isEmpty()) {
            logger.warn("Empty message received");
            return Flux.just("Please provide a non-empty message.");
        }
        
        logger.info("Processing chat message: {}", message);
        
        try {
            // Wrap the entire operation in a try/catch to handle any unexpected exceptions
            return chatModel.stream(new Prompt(message))
                    // Use flatMap instead of map to handle each response safely 
                    .flatMap(response -> {
                        try {
                            // Wrap the response extraction in a Mono to handle nulls gracefully
                            return Mono.justOrEmpty(Optional.ofNullable(response)
                                    .map(r -> r.getResult())
                                    .map(result -> result.getOutput())
                                    .map(output -> output.getText()))
                                    .switchIfEmpty(Mono.just("")); // Return empty string if any part is null
                        } catch (Exception e) {
                            logger.error("Error extracting text from response: {}", e.getMessage());
                            return Mono.just(""); // Return empty string on any exception
                        }
                    })
                    // Add a filter stage to remove empty strings
                    .filter(text -> text != null && !text.isEmpty())
                    // Handle any errors in the stream processing 
                    .onErrorResume(e -> {
                        logger.error("Error in AI model stream processing", e);
                        return Flux.just("Sorry, there was an error processing your request: " + e.getMessage());
                    })
                    // Add a defaultIfEmpty for cases where the entire stream might be empty
                    .defaultIfEmpty("No response generated. Please try again.");
        } catch (Exception e) {
            logger.error("Fatal error initializing chat stream", e);
            return Flux.just("Sorry, there was a critical error: " + e.getMessage());
        }
    }
} 
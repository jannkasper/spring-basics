package com.kasper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
        
        return chatModel.stream(new Prompt(message))
                .takeUntil(response -> {
                    Object finishReason = response.getMetadata().get("finishReason");
                    boolean isStop = finishReason != null && "STOP".equals(finishReason.toString());
                    
                    if (isStop) {
                        logger.debug("AI response complete with finishReason: STOP");
                    }
                    
                    return isStop;
                })
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return text != null ? text : "";
                })
                .filter(text -> !text.isEmpty())
                .onErrorResume(e -> {
                    logger.error("Error in AI model stream processing", e);
                    return Flux.just("Sorry, there was an error: " + e.getMessage());
                });
    }
} 
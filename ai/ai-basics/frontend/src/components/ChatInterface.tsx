import { useState, useRef, useEffect } from "react";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Card } from "./ui/card";
import { ChatMessage } from "./ChatMessage";
import { sendMessageToAI, streamChatFromAI } from "../lib/api";

type Message = {
  id: string;
  text: string;
  isUser: boolean;
  timestamp: string;
};

export function ChatInterface() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      text: "Hello! I'm your AI assistant. How can I help you today?",
      isUser: false,
      timestamp: new Date().toLocaleTimeString(),
    },
  ]);
  const [inputValue, setInputValue] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isStreaming, setIsStreaming] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const closeStreamRef = useRef<(() => void) | null>(null);

  // Scroll to bottom when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Clean up the EventSource on unmount
  useEffect(() => {
    return () => {
      if (closeStreamRef.current) {
        closeStreamRef.current();
      }
    };
  }, []);

  const handleSendMessage = async () => {
    if (inputValue.trim() === "" || isLoading) return;

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputValue,
      isUser: true,
      timestamp: new Date().toLocaleTimeString(),
    };

    setMessages((prev) => [...prev, userMessage]);
    const userInput = inputValue;
    setInputValue("");
    setIsLoading(true);

    // Try using the streaming endpoint first
    try {
      setIsStreaming(true);
      
      // Create a placeholder message for the streaming response
      const streamingMessageId = Date.now().toString();
      const streamingMessage: Message = {
        id: streamingMessageId,
        text: "",
        isUser: false,
        timestamp: new Date().toLocaleTimeString(),
      };
      
      setMessages((prev) => [...prev, streamingMessage]);
      
      // Start streaming the response
      closeStreamRef.current = streamChatFromAI(
        userInput,
        (chunk) => {
          // Update the streaming message with each chunk
          setMessages((prev) => 
            prev.map((msg) => 
              msg.id === streamingMessageId 
                ? { ...msg, text: msg.text + chunk } 
                : msg
            )
          );
        },
        () => {
          // Completed streaming
          setIsStreaming(false);
          setIsLoading(false);
        }
      );
    } catch (error) {
      console.error("Streaming error:", error);
      setIsStreaming(false);
      
      // Fall back to non-streaming API if streaming fails
      try {
        // Get AI response from our API
        const aiResponse = await sendMessageToAI(userInput);
        
        const aiMessage: Message = {
          id: Date.now().toString(),
          text: aiResponse,
          isUser: false,
          timestamp: new Date().toLocaleTimeString(),
        };
        
        setMessages((prev) => [...prev, aiMessage]);
      } catch (error) {
        // Show error message
        const errorMessage: Message = {
          id: Date.now().toString(),
          text: "Sorry, there was an error processing your request.",
          isUser: false,
          timestamp: new Date().toLocaleTimeString(),
        };
        
        setMessages((prev) => [...prev, errorMessage]);
      } finally {
        setIsLoading(false);
      }
    }
  };

  const handleKeyDown = (e: any) => {
    if (e.key === "Enter") {
      handleSendMessage();
    }
  };

  return (
    <div className="flex flex-col h-[80vh] max-w-2xl mx-auto">
      <Card className="flex-1 p-4 overflow-y-auto mb-4 bg-gray-50">
        {messages.map((message) => (
          <ChatMessage
            key={message.id}
            message={message.text}
            isUser={message.isUser}
            timestamp={message.timestamp}
          />
        ))}
        <div ref={messagesEndRef} />
      </Card>
      <div className="flex gap-2">
        <Input
          type="text"
          placeholder="Type your message..."
          value={inputValue}
          onChange={(e: any) => setInputValue(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={isLoading}
          className="flex-1"
        />
        <Button 
          onClick={() => handleSendMessage()} 
          disabled={inputValue.trim() === "" || isLoading}
        >
          {isStreaming ? "Receiving..." : isLoading ? "Sending..." : "Send"}
        </Button>
      </div>
    </div>
  );
} 
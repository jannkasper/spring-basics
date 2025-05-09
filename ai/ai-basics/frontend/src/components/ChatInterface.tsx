import { useState, useRef, useEffect } from "react";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { ChatMessage } from "./ChatMessage";
import { streamChatFromAI } from "../lib/api";

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

  // Scroll to bottom whenever messages change
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
    const userMessage = {
      id: Date.now().toString(),
      text: inputValue,
      isUser: true,
      timestamp: new Date().toLocaleTimeString(),
    };

    // Create placeholder for AI response
    const aiMessage = {
      id: (Date.now() + 1).toString(),
      text: "",
      isUser: false,
      timestamp: new Date().toLocaleTimeString(),
    };

    // Add both messages at once
    setMessages(prev => [...prev, userMessage, aiMessage]);
    
    const userInput = inputValue;
    setInputValue("");
    setIsLoading(true);
    setIsStreaming(true);
    
    try {
      // Stream the response
      closeStreamRef.current = streamChatFromAI(
        userInput,
        (chunk) => {
          // Update the last message (which is the AI message)
          setMessages(prev => {
            const newMessages = [...prev];
            const lastMessage = newMessages[newMessages.length - 1];
            if (!lastMessage.isUser) {
              newMessages[newMessages.length - 1] = {
                ...lastMessage,
                text: lastMessage.text + chunk
              };
            }
            return newMessages;
          });
        },
        () => {
          setIsStreaming(false);
          setIsLoading(false);
        }
      );
    } catch (error) {
      console.error("Error:", error);
      setIsStreaming(false);
      setIsLoading(false);
      
      // Update the AI message with an error
      setMessages(prev => {
        const newMessages = [...prev];
        const lastMessage = newMessages[newMessages.length - 1];
        if (!lastMessage.isUser) {
          newMessages[newMessages.length - 1] = {
            ...lastMessage,
            text: "Sorry, there was an error processing your request."
          };
        }
        return newMessages;
      });
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSendMessage();
    }
  };

  return (
    <div className="flex flex-col h-screen w-full bg-white">
      <div className="flex-1 overflow-y-auto pb-36 pt-0 mt-0">
        <div className="flex flex-col justify-end min-h-full">
          {/* Unified container for both messages and input */}
          <div className="w-full max-w-4xl mx-auto px-4">
            {/* Messages */}
            {messages.map((message) => (
              <ChatMessage
                key={message.id}
                message={message.text}
                isUser={message.isUser}
                timestamp={message.timestamp}
              />
            ))}
            <div ref={messagesEndRef} />
          </div>
        </div>
      </div>
      {/* Fixed input area, aligned with messages */}
      <div className="fixed bottom-0 left-0 right-0 border-gray-200 pb-4">
        <div className="absolute inset-0 w-full h-full bg-white -z-10 mt-4"/>
        <div className="w-full max-w-4xl mx-auto px-4 flex">
          <div className="relative w-full bg-white rounded-2xl">
            <Input
                type="text"
                placeholder="Message..."
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyDown={handleKeyDown}
                disabled={isLoading}
                className="w-full px-4 py-6 rounded-2xl pr-24 shadow-sm border border-gray-300"
            />
            <Button
                onClick={handleSendMessage}
                disabled={inputValue.trim() === "" || isLoading}
                className="absolute right-2 top-1/2 -translate-y-1/2 rounded-xl"
                variant="ghost"
                size="sm"
            >
              {isStreaming ? "..." : isLoading ? "..." : "Send"}
            </Button>
          </div>
        </div>
        <div className="w-full max-w-4xl mx-auto px-4 mt-2 text-xs text-center text-gray-400">
          AI chat assistant may produce inaccurate information
        </div>
      </div>
    </div>
  );
} 
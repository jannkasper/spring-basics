// In development, we can use the proxy set up in vite.config.ts
// In production, the app is served from Spring Boot, so we use a relative path
const API_BASE_URL = '/api';

export async function sendMessageToAI(message: string): Promise<string> {
  try {
    // Call the Spring Boot backend
    const response = await fetch(`${API_BASE_URL}/chat?message=${encodeURIComponent(message)}`);
    
    if (!response.ok) {
      throw new Error(`Server responded with status: ${response.status}`);
    }
    
    const data = await response.json();
    
    if (data.result === 'ERROR') {
      throw new Error(data.message);
    }
    
    // The actual implementation would handle the flux response properly
    // This is a simplified version
    return data.data || "No response from server";
    
  } catch (error) {
    console.error('Error sending message:', error);
    return "Sorry, there was an error processing your request.";
  }
}

// Add a streaming version of the API call
export function streamChatFromAI(message: string, onChunk: (chunk: string) => void, onComplete: () => void) {
  const eventSource = new EventSource(`${API_BASE_URL}/chat/stream?message=${encodeURIComponent(message)}`);
  
  eventSource.onmessage = (event) => {
    onChunk(event.data);
  };
  
  eventSource.onerror = (error) => {
    console.error('EventSource error:', error);
    eventSource.close();
    onComplete();
  };
  
  // Return a function to close the connection
  return () => {
    eventSource.close();
  };
} 
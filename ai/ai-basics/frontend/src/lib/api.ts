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
  
  // Track processed event IDs to prevent duplicates
  const processedEventIds = new Set<string>();
  let fullText = '';
  let lastCharWasAlphanumeric = false;
  
  eventSource.onmessage = (event) => {
    // Check if we've already processed this event by ID
    if (event.lastEventId && processedEventIds.has(event.lastEventId)) {
      return; // Skip duplicate events
    }
    
    // Mark this event as processed
    if (event.lastEventId) {
      processedEventIds.add(event.lastEventId);
    }
    
    // Process the chunk
    if (event.data) {
      let chunk = event.data;
      
      // Add space if needed to ensure proper word boundaries
      // Only add a space if the last character of previous text is alphanumeric
      // and the first character of this chunk is alphanumeric 
      // (suggesting two words that should be separated)
      if (fullText.length > 0 && lastCharWasAlphanumeric && 
          chunk.length > 0 && /^[a-zA-Z0-9]/.test(chunk)) {
        // Check if last char of full text is alphanumeric and there's no space already
        if (/[a-zA-Z0-9]$/.test(fullText) && !fullText.endsWith(' ')) {
          chunk = ' ' + chunk;
        }
      }
      
      // Basic space fixing for common patterns
      chunk = chunk
        // Fix markdown headers (###Header → ### Header)
        .replace(/^([#]+)([A-Za-z0-9])/g, '$1 $2')
        // Fix markdown lists
        .replace(/^([0-9]+\.)([a-zA-Z])/g, '$1 $2')
        .replace(/^(\-)([a-zA-Z])/g, '$1 $2');
      
      // Send processed chunk to UI
      onChunk(chunk);
      
      // Update full text and track the last character's nature
      fullText += chunk;
      if (chunk.length > 0) {
        lastCharWasAlphanumeric = /[a-zA-Z0-9]$/.test(chunk);
      }
    }
  };
  
  eventSource.onerror = (error) => {
    console.error('EventSource error:', error);
    eventSource.close();
    
    if (fullText.trim() === '') {
      // If we received no text, add a fallback message
      onChunk("Sorry, there was an error processing your request.");
    }
    
    onComplete();
  };
  
  // Return a function to close the connection
  return () => {
    eventSource.close();
  };
}
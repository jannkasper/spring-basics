document.addEventListener('DOMContentLoaded', () => {
    const chatMessages = document.getElementById('chat-messages');
    const messageForm = document.getElementById('message-form');
    const messageInput = document.getElementById('message-input');
    const sendButton = document.getElementById('send-button');
    
    // Store the original favicon
    const originalFavicon = document.querySelector('link[rel="icon"]')?.href || '/favicon.ico';
    
    // Function to change favicon (indicating loading state)
    function setLoadingFavicon(isLoading) {
        const favicon = document.querySelector('link[rel="icon"]');
        if (!favicon) {
            const newFavicon = document.createElement('link');
            newFavicon.rel = 'icon';
            newFavicon.href = isLoading ? '/img/loading-favicon.ico' : originalFavicon;
            document.head.appendChild(newFavicon);
        } else {
            favicon.href = isLoading ? '/img/loading-favicon.ico' : originalFavicon;
        }
    }

    // Auto-resize the textarea
    messageInput.addEventListener('input', () => {
        messageInput.style.height = 'auto';
        messageInput.style.height = (messageInput.scrollHeight) + 'px';
    });

    // Submit the form when Enter is pressed (but allow Shift+Enter for newlines)
    messageInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            messageForm.dispatchEvent(new Event('submit'));
        }
    });

    // Handle form submission
    messageForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const message = messageInput.value.trim();
        if (!message) return;

        // Add user message to chat
        addMessage(message, 'user');

        // Clear input and reset height
        messageInput.value = '';
        messageInput.style.height = 'auto';
        messageInput.focus();

        // Disable input while waiting for response
        setFormDisabled(true);
        
        // Change favicon to loading state
        setLoadingFavicon(true);

        // Add typing indicator
        const typingIndicator = addTypingIndicator();

        try {
            // Create an AI message container first
            const aiMessageDiv = document.createElement('div');
            aiMessageDiv.classList.add('message', 'ai-message');
            const aiMessageContent = document.createElement('div');
            aiMessageContent.classList.add('message-content');
            
            // Create a paragraph for the message text with a cursor element
            const messageTextEl = document.createElement('div');
            messageTextEl.classList.add('message-text');
            
            // Create stream cursor element (blinking cursor effect)
            const cursorEl = document.createElement('span');
            cursorEl.classList.add('stream-cursor');
            
            messageTextEl.appendChild(cursorEl);
            aiMessageContent.appendChild(messageTextEl);
            aiMessageDiv.appendChild(aiMessageContent);
            
            // Remove typing indicator before showing the actual message
            typingIndicator.remove();
            
            // Append the message container to the chat
            chatMessages.appendChild(aiMessageDiv);
            
            // Scroll to the new message
            chatMessages.scrollTop = chatMessages.scrollHeight;
            
            // Connect to SSE stream
            const eventSource = new EventSource(`/api/chat/stream?message=${encodeURIComponent(message)}`);
            
            let messageText = '';
            let formattedText = '';
            let codeBlockOpen = false;
            let inlineCodeOpen = false;
            
            eventSource.onmessage = (event) => {
                const chunk = event.data;
                
                // Check if the chunk is an error message
                if (chunk.startsWith('Error:')) {
                    messageTextEl.innerHTML = `<span class="error-message">${chunk}</span>`;
                    // Remove cursor for error messages
                    if (cursorEl.parentNode) {
                        cursorEl.remove();
                    }
                    
                    // Close connection since we got an error
                    eventSource.close();
                    setLoadingFavicon(false);
                    setFormDisabled(false);
                    return;
                }
                
                // Update the full message text
                if (messageText) {
                    const currentText = messageText;
                    const needsSpace = currentText && 
                                   !currentText.endsWith(' ') && 
                                   !chunk.startsWith(' ') && 
                                   !chunk.match(/^[.,!?;:)]/) && 
                                   !currentText.endsWith('\n') &&
                                   !currentText.match(/[([{]$/);
                    
                    messageText += (needsSpace ? ' ' : '') + chunk;
                } else {
                    messageText = chunk;
                }
                
                // Process the entire text with markdown formatting
                formattedText = processMarkdown(messageText);
                
                // Update the message content
                messageTextEl.innerHTML = formattedText;
                
                // Add cursor at the end
                messageTextEl.appendChild(cursorEl);
                
                // Scroll to see the latest content
                chatMessages.scrollTop = chatMessages.scrollHeight;
            };
            
            eventSource.onerror = (error) => {
                console.error('SSE Error:', error);
                eventSource.close();
                
                // Remove stream cursor when done
                if (cursorEl.parentNode) {
                    cursorEl.remove();
                }
                
                // If the message is empty, show an error
                if (!messageText) {
                    messageTextEl.textContent = 'Sorry, there was an error processing your request.';
                    messageTextEl.classList.add('error');
                }
                
                // Reset loading state
                setLoadingFavicon(false);
                
                // Re-enable the form
                setFormDisabled(false);
            };
            
            eventSource.addEventListener('complete', (event) => {
                // Remove stream cursor when done
                if (cursorEl.parentNode) {
                    cursorEl.remove();
                }
                
                eventSource.close();
                setLoadingFavicon(false);
                setFormDisabled(false);
            });
            
            // Close the connection after 60 seconds (safety measure)
            setTimeout(() => {
                if (eventSource.readyState !== EventSource.CLOSED) {
                    if (cursorEl.parentNode) {
                        cursorEl.remove();
                    }
                    
                    eventSource.close();
                    setLoadingFavicon(false);
                    setFormDisabled(false);
                }
            }, 60000);
            
        } catch (error) {
            console.error('Error:', error);
            
            // Remove typing indicator
            typingIndicator.remove();
            
            // Show error message
            addMessage(`Sorry, there was an error: ${error.message}`, 'ai', true);
            
            // Reset loading state
            setLoadingFavicon(false);
            
            // Re-enable the form
            setFormDisabled(false);
        }
    });

    // Function to process markdown
    function processMarkdown(text) {
        let processed = text;
        
        // Process code blocks (```)
        processed = processed.replace(/```([^`]*?)```/g, function(match, codeContent) {
            return `<pre class="code-block"><code>${codeContent.trim()}</code></pre>`;
        });
        
        // Process inline code (`)
        processed = processed.replace(/`([^`]+?)`/g, function(match, codeContent) {
            return `<code class="inline-code">${codeContent}</code>`;
        });
        
        // Process bold text (**)
        processed = processed.replace(/\*\*([^*]+?)\*\*/g, function(match, content) {
            return `<strong>${content}</strong>`;
        });
        
        // Process italic text (*)
        processed = processed.replace(/\*([^*]+?)\*/g, function(match, content) {
            return `<em>${content}</em>`;
        });
        
        // Process headers (###)
        processed = processed.replace(/### ([^\n]+)/g, function(match, content) {
            return `<h3>${content.trim()}</h3>`;
        });
        
        // Process headers (##)
        processed = processed.replace(/## ([^\n]+)/g, function(match, content) {
            return `<h2>${content.trim()}</h2>`;
        });
        
        // Process headers (#)
        processed = processed.replace(/# ([^\n]+)/g, function(match, content) {
            return `<h1>${content.trim()}</h1>`;
        });
        
        // Fix hyphenation issues (convert -word to a proper hyphen)
        processed = processed.replace(/(\w+)(?:\s+)?-(?:\s+)?(\w+)/g, '$1-$2');
        
        // Process line breaks
        processed = processed.replace(/\n/g, '<br>');
        
        return processed;
    }

    // Function to add a message to the chat
    function addMessage(content, sender, isError = false) {
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('message', `${sender}-message`);
        
        const messageContent = document.createElement('div');
        messageContent.classList.add('message-content');
        if (isError) messageContent.classList.add('error');
        
        const messageText = document.createElement('div');
        messageText.classList.add('message-text');
        
        // Only apply markdown formatting to AI messages
        if (sender === 'ai') {
            messageText.innerHTML = processMarkdown(content);
        } else {
            messageText.textContent = content;
        }
        
        messageContent.appendChild(messageText);
        messageDiv.appendChild(messageContent);
        chatMessages.appendChild(messageDiv);
        
        // Scroll to bottom
        chatMessages.scrollTop = chatMessages.scrollHeight;
        
        return messageDiv;
    }

    // Function to add typing indicator
    function addTypingIndicator() {
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('message', 'ai-message');
        messageDiv.id = 'typing-indicator';
        
        const indicator = document.createElement('div');
        indicator.classList.add('typing-indicator');
        
        for (let i = 0; i < 3; i++) {
            const dot = document.createElement('span');
            indicator.appendChild(dot);
        }
        
        messageDiv.appendChild(indicator);
        chatMessages.appendChild(messageDiv);
        
        // Scroll to bottom
        chatMessages.scrollTop = chatMessages.scrollHeight;
        
        return messageDiv;
    }

    // Function to set form disabled state
    function setFormDisabled(disabled) {
        messageInput.disabled = disabled;
        sendButton.disabled = disabled;
    }

    // Add CSS for error messages in the head 
    const style = document.createElement('style');
    style.textContent = `
        .error-message {
            color: #e53e3e;
            font-weight: 500;
        }
    `;
    document.head.appendChild(style);
}); 
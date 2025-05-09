import ReactMarkdown from "react-markdown";

type ChatMessageProps = {
  message: string;
  isUser: boolean;
  timestamp: string;
};

// Function to fix spaces between words in AI responses as a fallback
const fixSpacing = (text: string): string => {
  if (!text) return text;
  
  // Simple fallback for any residual spacing issues
  return text
    // Add space between lowercase followed by uppercase (camelCase → camel Case)
    .replace(/([a-z])([A-Z])/g, '$1 $2')
    // Add space after punctuation if followed by a letter
    .replace(/([.,;:!?()])([a-zA-Z0-9])/g, '$1 $2')
    // Fix markdown headers (###Header → ### Header)
    .replace(/([#]+)([a-zA-Z0-9])/g, '$1 $2');
};

export function ChatMessage({ message, isUser }: ChatMessageProps) {
  // Process AI messages for spacing issues as a fallback
  const processedMessage = isUser ? message : fixSpacing(message);
  
  return (
    <div className={`py-4 ${isUser ? 'flex justify-end' : 'w-full'}`}>
      <div className={`${isUser ? 'flex max-w-[90%] sm:max-w-[75%] min-w-[120px] flex-row-reverse gap-3 items-start' : 'w-full'}`}>
        <div className={`
          py-3
          ${isUser ? 'px-4': ''}
          ${isUser ? 'bg-gray-100 text-gray-800' : 'bg-transparent text-gray-800'} 
          ${isUser ? 'rounded-2xl' : ''}
          ${isUser ? 'shadow-sm' : ''}
          overflow-hidden
          ${isUser ? 'max-w-full' : 'w-full'}
        `}>
          {isUser ? (
            <p className="whitespace-pre-wrap break-words text-sm overflow-wrap-break-word">{processedMessage}</p>
          ) : (
            <div className="prose prose-sm prose-headings:mt-2 prose-headings:mb-1 prose-p:my-1 prose-ul:my-1 prose-ol:my-1 prose-li:my-0.5 prose-pre:my-1 max-w-none text-sm overflow-wrap-break-word">
              <ReactMarkdown>{processedMessage}</ReactMarkdown>
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 
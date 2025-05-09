import ReactMarkdown from "react-markdown";
import { Avatar, AvatarFallback } from "./ui/avatar";

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

export function ChatMessage({ message, isUser, timestamp }: ChatMessageProps) {
  // Process AI messages for spacing issues as a fallback
  const processedMessage = isUser ? message : fixSpacing(message);
  
  return (
    <div className={`py-4 ${isUser ? 'flex justify-end' : 'flex justify-start'}`}>
      <div className={`flex max-w-[90%] sm:max-w-[75%] min-w-[120px] ${isUser ? 'flex-row-reverse' : 'flex-row'} gap-3 items-start`}>
        <Avatar className={`h-8 w-8 shrink-0 mt-1 ${isUser ? 'bg-blue-600' : 'bg-emerald-600'}`}>
          <AvatarFallback>{isUser ? 'U' : 'AI'}</AvatarFallback>
        </Avatar>
        
        <div className={`
          py-3 px-4 
          ${isUser ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-800'} 
          ${isUser ? 'rounded-2xl rounded-tr-none' : 'rounded-2xl rounded-tl-none'}
          shadow-sm
          overflow-hidden
          max-w-full
        `}>
          {isUser ? (
            <p className="whitespace-pre-wrap break-words text-sm overflow-wrap-break-word">{processedMessage}</p>
          ) : (
            <div className="prose prose-sm prose-headings:mt-2 prose-headings:mb-1 prose-p:my-1 prose-ul:my-1 prose-ol:my-1 prose-li:my-0.5 prose-pre:my-1 max-w-none text-sm overflow-wrap-break-word">
              <ReactMarkdown>{processedMessage}</ReactMarkdown>
            </div>
          )}
          <div className={`text-xs mt-2 ${isUser ? 'text-blue-200' : 'text-gray-500'}`}>
            {timestamp}
          </div>
        </div>
      </div>
    </div>
  );
} 
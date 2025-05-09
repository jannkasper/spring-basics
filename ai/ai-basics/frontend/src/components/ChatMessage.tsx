import { Avatar, AvatarFallback } from "./ui/avatar";
import { Card } from "./ui/card";

type ChatMessageProps = {
  message: string;
  isUser: boolean;
  timestamp?: string;
};

export function ChatMessage({ message, isUser, timestamp }: ChatMessageProps) {
  return (
    <div className={`flex ${isUser ? "justify-end" : "justify-start"} mb-4`}>
      <div className={`flex items-start ${isUser ? "flex-row-reverse" : "flex-row"} gap-2 max-w-[80%]`}>
        <Avatar className={`h-8 w-8 ${isUser ? "bg-blue-500" : "bg-violet-600"}`}>
          <AvatarFallback>{isUser ? "U" : "AI"}</AvatarFallback>
        </Avatar>
        <Card className={`p-3 ${isUser ? "bg-blue-100" : "bg-violet-50"}`}>
          <p className="text-sm">{message}</p>
          {timestamp && <span className="text-xs text-gray-500 mt-1 block">{timestamp}</span>}
        </Card>
      </div>
    </div>
  );
} 
import { ChatInterface } from './components/ChatInterface'
import './App.css'

function App() {
  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <header className="text-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800">AI Chat Assistant</h1>
        <p className="text-gray-600">Ask me anything and I'll do my best to help</p>
      </header>
      <main>
        <ChatInterface />
      </main>
      <footer className="text-center mt-8 text-sm text-gray-500">
        &copy; {new Date().getFullYear()} AI Chat Demo
      </footer>
    </div>
  )
}

export default App

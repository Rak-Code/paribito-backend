# Groq AI Chat - Frontend Implementation Guide

## Overview
This guide provides everything you need to integrate the Groq AI Chat API into your frontend application. The API is public (no authentication required) and accepts simple message-based requests.

## API Endpoint

```
POST /api/chat
```

**Base URL:** `http://your-domain.com` or `http://localhost:8080` (development)

**Authentication:** None required (public endpoint)

## Request Format

### Simple Request
```json
{
  "message": "Your question or prompt here"
}
```

### Example Request
```json
{
  "message": "Explain what is Spring Boot in simple terms"
}
```

## Response Format

```json
{
  "response": "AI generated response text",
  "model": "llama-3.3-70b-versatile",
  "tokensUsed": 150
}
```

## Implementation Examples

### 1. Vanilla JavaScript (Fetch API)

```javascript
async function sendMessage(userMessage) {
  try {
    const response = await fetch('http://localhost:8080/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        message: userMessage
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data.response;
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
}

// Usage
sendMessage('What is artificial intelligence?')
  .then(response => console.log(response))
  .catch(error => console.error('Failed:', error));
```

### 2. React Implementation

```jsx
import React, { useState } from 'react';

function ChatComponent() {
  const [message, setMessage] = useState('');
  const [response, setResponse] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const sendMessage = async () => {
    if (!message.trim()) return;

    setLoading(true);
    setError(null);

    try {
      const res = await fetch('http://localhost:8080/api/chat', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message }),
      });

      if (!res.ok) {
        throw new Error('Failed to get response');
      }

      const data = await res.json();
      setResponse(data.response);
      setMessage('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-container">
      <div className="chat-output">
        {response && (
          <div className="ai-response">
            <strong>AI:</strong> {response}
          </div>
        )}
        {error && <div className="error">{error}</div>}
      </div>

      <div className="chat-input">
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Ask me anything..."
          disabled={loading}
        />
        <button onClick={sendMessage} disabled={loading || !message.trim()}>
          {loading ? 'Sending...' : 'Send'}
        </button>
      </div>
    </div>
  );
}

export default ChatComponent;
```

### 3. React with Axios

```jsx
import React, { useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/chat';

function ChatWithAxios() {
  const [message, setMessage] = useState('');
  const [chatHistory, setChatHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!message.trim()) return;

    const userMessage = message;
    setMessage('');
    setLoading(true);

    // Add user message to history
    setChatHistory(prev => [...prev, { role: 'user', content: userMessage }]);

    try {
      const response = await axios.post(API_URL, { message: userMessage });
      
      // Add AI response to history
      setChatHistory(prev => [
        ...prev,
        { role: 'ai', content: response.data.response }
      ]);
    } catch (error) {
      console.error('Error:', error);
      setChatHistory(prev => [
        ...prev,
        { role: 'error', content: 'Failed to get response. Please try again.' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-app">
      <div className="chat-messages">
        {chatHistory.map((msg, index) => (
          <div key={index} className={`message ${msg.role}`}>
            <strong>{msg.role === 'user' ? 'You' : 'AI'}:</strong>
            <p>{msg.content}</p>
          </div>
        ))}
        {loading && <div className="loading">AI is thinking...</div>}
      </div>

      <form onSubmit={sendMessage} className="chat-form">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Type your message..."
          disabled={loading}
        />
        <button type="submit" disabled={loading || !message.trim()}>
          Send
        </button>
      </form>
    </div>
  );
}

export default ChatWithAxios;
```

### 4. Vue.js Implementation

```vue
<template>
  <div class="chat-container">
    <div class="messages">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
        <strong>{{ msg.role === 'user' ? 'You' : 'AI' }}:</strong>
        <p>{{ msg.content }}</p>
      </div>
      <div v-if="loading" class="loading">Thinking...</div>
    </div>

    <div class="input-area">
      <input
        v-model="currentMessage"
        @keyup.enter="sendMessage"
        placeholder="Ask me anything..."
        :disabled="loading"
      />
      <button @click="sendMessage" :disabled="loading || !currentMessage.trim()">
        Send
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentMessage: '',
      messages: [],
      loading: false
    };
  },
  methods: {
    async sendMessage() {
      if (!this.currentMessage.trim()) return;

      const userMessage = this.currentMessage;
      this.messages.push({ role: 'user', content: userMessage });
      this.currentMessage = '';
      this.loading = true;

      try {
        const response = await fetch('http://localhost:8080/api/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ message: userMessage })
        });

        const data = await response.json();
        this.messages.push({ role: 'ai', content: data.response });
      } catch (error) {
        console.error('Error:', error);
        this.messages.push({ 
          role: 'error', 
          content: 'Failed to get response' 
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
```

### 5. Angular Implementation

```typescript
// chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ChatResponse {
  response: string;
  model: string;
  tokensUsed: number;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) {}

  sendMessage(message: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(this.apiUrl, { message });
  }
}

// chat.component.ts
import { Component } from '@angular/core';
import { ChatService } from './chat.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html'
})
export class ChatComponent {
  message = '';
  response = '';
  loading = false;
  error = '';

  constructor(private chatService: ChatService) {}

  sendMessage() {
    if (!this.message.trim()) return;

    this.loading = true;
    this.error = '';

    this.chatService.sendMessage(this.message).subscribe({
      next: (data) => {
        this.response = data.response;
        this.message = '';
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to get response';
        this.loading = false;
      }
    });
  }
}
```

## Complete HTML + CSS + JS Example

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Groq AI Chat</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      height: 100vh;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .chat-container {
      width: 90%;
      max-width: 600px;
      height: 80vh;
      background: white;
      border-radius: 20px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.2);
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .chat-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 20px;
      text-align: center;
      font-size: 1.5em;
      font-weight: bold;
    }

    .chat-messages {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      background: #f5f5f5;
    }

    .message {
      margin-bottom: 15px;
      padding: 12px 16px;
      border-radius: 12px;
      max-width: 80%;
      word-wrap: break-word;
      animation: fadeIn 0.3s;
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(10px); }
      to { opacity: 1; transform: translateY(0); }
    }

    .message.user {
      background: #667eea;
      color: white;
      margin-left: auto;
      text-align: right;
    }

    .message.ai {
      background: white;
      color: #333;
      border: 1px solid #e0e0e0;
    }

    .message.error {
      background: #ff6b6b;
      color: white;
    }

    .loading {
      text-align: center;
      color: #667eea;
      font-style: italic;
      padding: 10px;
    }

    .chat-input-area {
      padding: 20px;
      background: white;
      border-top: 1px solid #e0e0e0;
      display: flex;
      gap: 10px;
    }

    #messageInput {
      flex: 1;
      padding: 12px 16px;
      border: 2px solid #e0e0e0;
      border-radius: 25px;
      font-size: 14px;
      outline: none;
      transition: border-color 0.3s;
    }

    #messageInput:focus {
      border-color: #667eea;
    }

    #sendButton {
      padding: 12px 30px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 25px;
      cursor: pointer;
      font-weight: bold;
      transition: transform 0.2s;
    }

    #sendButton:hover:not(:disabled) {
      transform: scale(1.05);
    }

    #sendButton:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  </style>
</head>
<body>
  <div class="chat-container">
    <div class="chat-header">
      🤖 Groq AI Chat
    </div>
    <div class="chat-messages" id="chatMessages"></div>
    <div class="chat-input-area">
      <input 
        type="text" 
        id="messageInput" 
        placeholder="Ask me anything..."
        autocomplete="off"
      />
      <button id="sendButton">Send</button>
    </div>
  </div>

  <script>
    const API_URL = 'http://localhost:8080/api/chat';
    const chatMessages = document.getElementById('chatMessages');
    const messageInput = document.getElementById('messageInput');
    const sendButton = document.getElementById('sendButton');

    function addMessage(content, type) {
      const messageDiv = document.createElement('div');
      messageDiv.className = `message ${type}`;
      messageDiv.textContent = content;
      chatMessages.appendChild(messageDiv);
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    function showLoading() {
      const loadingDiv = document.createElement('div');
      loadingDiv.className = 'loading';
      loadingDiv.id = 'loading';
      loadingDiv.textContent = '🤔 AI is thinking...';
      chatMessages.appendChild(loadingDiv);
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    function hideLoading() {
      const loadingDiv = document.getElementById('loading');
      if (loadingDiv) loadingDiv.remove();
    }

    async function sendMessage() {
      const message = messageInput.value.trim();
      if (!message) return;

      // Disable input
      messageInput.disabled = true;
      sendButton.disabled = true;

      // Add user message
      addMessage(message, 'user');
      messageInput.value = '';

      // Show loading
      showLoading();

      try {
        const response = await fetch(API_URL, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ message })
        });

        if (!response.ok) {
          throw new Error('Failed to get response');
        }

        const data = await response.json();
        hideLoading();
        addMessage(data.response, 'ai');
      } catch (error) {
        hideLoading();
        addMessage('Sorry, something went wrong. Please try again.', 'error');
        console.error('Error:', error);
      } finally {
        messageInput.disabled = false;
        sendButton.disabled = false;
        messageInput.focus();
      }
    }

    // Event listeners
    sendButton.addEventListener('click', sendMessage);
    messageInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') sendMessage();
    });

    // Welcome message
    addMessage('Hello! I\'m your AI assistant. Ask me anything!', 'ai');
  </script>
</body>
</html>
```

## Configuration

### Development Environment
```javascript
const API_URL = 'http://localhost:8080/api/chat';
```

### Production Environment
```javascript
const API_URL = 'https://your-domain.com/api/chat';
```

### Environment Variables (React)
```javascript
// .env
REACT_APP_API_URL=http://localhost:8080

// Usage
const API_URL = `${process.env.REACT_APP_API_URL}/api/chat`;
```

## Error Handling

```javascript
async function sendMessageWithErrorHandling(message) {
  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message })
    });

    if (!response.ok) {
      if (response.status === 500) {
        throw new Error('Server error. Please try again later.');
      } else if (response.status === 400) {
        throw new Error('Invalid message format.');
      } else {
        throw new Error('Something went wrong.');
      }
    }

    return await response.json();
  } catch (error) {
    if (error.name === 'TypeError') {
      throw new Error('Network error. Check your connection.');
    }
    throw error;
  }
}
```

## CORS Configuration

If you encounter CORS issues during development, you may need to configure your backend or use a proxy.

### React Proxy (package.json)
```json
{
  "proxy": "http://localhost:8080"
}
```

Then use relative URLs:
```javascript
fetch('/api/chat', { ... })
```

## Testing with cURL

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "What is Spring Boot?"}'
```

## API Features

- **Model:** llama-3.3-70b-versatile (configured server-side)
- **Max Tokens:** 8000 (increased for better context)
- **Authentication:** None required
- **Rate Limiting:** Check with your backend configuration

## Best Practices

1. **Input Validation:** Always validate user input before sending
2. **Loading States:** Show loading indicators during API calls
3. **Error Handling:** Implement proper error handling and user feedback
4. **Debouncing:** Consider debouncing for real-time features
5. **Message History:** Store chat history in state/local storage
6. **Accessibility:** Ensure keyboard navigation and screen reader support

## Next Steps

1. Test the API endpoint using Swagger UI at `http://localhost:8080/swagger-ui/index.html`
2. Choose your preferred frontend framework
3. Copy the relevant code example
4. Update the API_URL to match your environment
5. Customize the UI to match your design

## Support

For backend issues or API questions, refer to the main project documentation or contact the backend team.

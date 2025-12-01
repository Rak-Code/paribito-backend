# Groq Chat Integration Guide

## Setup Complete ✅

A simple Groq AI chatbot has been integrated into your e-commerce application.

## Files Created

1. **GroqChatService.java** - Main service for Groq API calls
2. **ChatController.java** - REST endpoint for chat
3. **ChatRequest.java** - Request DTO
4. **ChatResponse.java** - Response DTO

## Configuration

### 1. Get Your Groq API Key
- Visit: https://console.groq.com/keys
- Create a free account
- Generate an API key

### 2. Update .env File
```properties
GROQ_API_KEY=your_actual_groq_api_key_here
```

## API Endpoint

### POST /api/chat
Send a message to the Groq AI chatbot.

**Request:**
```json
{
  "message": "What is Spring Boot?",
  "model": "llama-3.3-70b-versatile"
}
```

**Response:**
```json
{
  "response": "Spring Boot is a framework...",
  "model": "llama-3.3-70b-versatile",
  "tokensUsed": 150
}
```

## Available Models

- `llama-3.3-70b-versatile` (Default - Best for general use)
- `llama-3.1-8b-instant` (Faster, lighter)
- `mixtral-8x7b-32768` (Good for long context)
- `gemma2-9b-it` (Google's Gemma model)

## Testing

### Using cURL:
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"Hello, how are you?\"}"
```

### Using Postman:
1. Method: POST
2. URL: `http://localhost:8080/api/chat`
3. Body (JSON):
```json
{
  "message": "Tell me about your e-commerce features"
}
```

## Features

- ✅ Simple and clean implementation
- ✅ Uses Groq's fast LLM API
- ✅ Configurable model selection
- ✅ Token usage tracking
- ✅ Swagger documentation included
- ✅ Error handling

## Next Steps

1. Add your Groq API key to `.env`
2. Restart your application
3. Test the endpoint at: http://localhost:8080/swagger-ui.html
4. Start chatting with the AI!

## Notes

- Groq offers free tier with generous limits
- Very fast inference (faster than OpenAI)
- No additional dependencies needed (uses existing RestTemplate)

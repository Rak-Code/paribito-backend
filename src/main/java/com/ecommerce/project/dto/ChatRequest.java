package com.ecommerce.project.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String model = "llama-3.3-70b-versatile"; // Default Groq model
}

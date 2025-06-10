# Spring AI Samples

A collection of Spring AI integration examples and utilities demonstrating various AI capabilities with Spring Boot
applications.

## Project Structure

```mermaid
flowchart TB
    subgraph "Main Project Components"
        MCP[MCP Module]
        RAG[RAG Module]
        AI_GW[AI Gateway]
        CHAT[Chat Module]
    end

    subgraph "Frontend"
        MINI_UI[Mini Chat UI]
    end

    subgraph "Infrastructure"
        LIBS[Shared Libraries]
    end


    %% Connections
    MINI_UI -->|Interacts with| AI_GW
    AI_GW -->|Orchestrates| MCP
    AI_GW -->|Orchestrates| RAG
    AI_GW -->|Orchestrates| CHAT
    LIBS -.->|Supports| MCP
    LIBS -.->|Supports| RAG
    LIBS -.->|Supports| CHAT
```

- **ai-gateway**: API Gateway for AI services
- **chat**: Chat applications implementations
    - ollama-chat: Basic Ollama chat implementation
    - ollama-chat-advanced: Advanced Ollama chat features
- **libs**: Shared libraries and starters
    - spring-webflux-ai-starter: Spring WebFlux integration for AI services
- **mcp**: Model Context Protocol (MCP)
- **rag**: Retrieval-Augmented Generation examples
- **mini-chat-ui**: Minimalistic chat user interface

## Documentation

Detailed documentation for each module can be found in their respective directories.



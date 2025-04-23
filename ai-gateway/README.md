# 🧠 Spring AI Gateway for Ollama Compatibility

A **Ollama-compatible API gateway** for exposing **Spring AI** endpoints as chat models.

This project lets you use **Spring AI tools** and **advisors** in a way that’s fully compatible with tools
like [Open WebUI](https://github.com/open-webui/open-webui), which expect
the [Ollama HTTP API](https://github.com/ollama/ollama/blob/main/docs/api.md).

---

## ✨ Features

- 🎯 **Ollama-compatible API**: Drop-in replacement for Ollama `/api/chat`, `/api/tags`, etc.
- ⚙️ **Spring AI tools & advisors**: Extend responses with Spring AI-native logic like function-calling or tool
  execution.
- 🌀 **Streaming support**: Full support for `application/x-ndjson` and Open WebUI’s streaming JSON format.
- 🔍 **Dynamic model discovery**: Aggregates available models across multiple Spring AI instances.
- 🔁 **Reverse proxying & orchestration**: Routes chat/generate requests to the right backend based on model name.

---

## 🧪 API Endpoints

These endpoints mimic the Ollama API spec:

| Endpoint           | Description                               |
|--------------------|-------------------------------------------|
| `GET /api/tags`    | Returns all available model tags          |
| `POST /api/chat`   | Starts a chat session (streaming support) |
| `GET /api/version` | Returns mock Ollama version               |

---

## ⚙️ Configuration

You can configure backend model sources using application.yml:

```yaml
ai:
  urls:
    - http://localhost:8081
    - http://localhost:8082
```

Each URL should expose /api/tags and /api/chat endpoints using Spring AI.

---

## 💡 Why This Exists

This gateway enables seamless integration of Java/Spring-based models into any Ollama-compatible UI or client:

* ✅ Use Open WebUI with Spring AI
* ✅ Benefit from Spring's reactive stack and tool ecosystem
* ✅ Centralize control of multiple model endpoints
* ✅ Extend capabilities with tools, advisors, and logic

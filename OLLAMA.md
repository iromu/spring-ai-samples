# Ollama Setup Guide

## Prerequisites

- Required: GPU with sufficient RAM
- Download Ollama: [Official Download Page](https://ollama.com/download)

## Installation Options

### Option 1: Local Installation

1. Download and install Ollama
2. Pull the LLama model:
   ```bash
   ollama pull llama3.1:8b
   ```

### Option 2: Docker Installation

Uncomment the ollama container configuration in `compose.yaml`

## Common Issues and Solutions

### Application Loading Issues

#### Spring Application Hanging

**Symptom**: Spring application doesn't complete loading  
**Solution**:

- Check Ollama container logs for the following message:
  ```
  time=2024-07-10T23:26:57.317Z level=INFO msg="waiting for server to become available" status="llm server loading model"
  ```
- Verify sufficient GPU RAM is available for model loading

#### Slow REST Endpoint Response

**Symptom**: REST endpoints have high latency  
**Solution**:

- Monitor Ollama logs for successful initialization:
  ```
  msg="llama runner started in 149.95 seconds"
  ```
- Endpoints will become responsive after this initialization message appears

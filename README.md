## Setup

For better experience, download ollama from https://ollama.com/download
and pull llama3 model.

Or you can uncomment the ollama container on compose.yaml

## Troubleshooting

### If Spring app does not finishes loading:

Check on docker ollama for the message

    2024-07-11 00:26:57 time=2024-07-10T23:26:57.317Z level=INFO source=server.go:594 msg="waiting for server to become available" status="llm server loading model"

*Check enough GPU RAM is available to load the model*

### Rest endpoints are taking too long

Check docker log. Rest endpoints will start replying when this message appears on ollama log:

    msg="llama runner started in 149.95 seconds"

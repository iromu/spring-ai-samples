$sw = [Diagnostics.Stopwatch]::StartNew()

# To activate custom dockerignore
$env:DOCKER_BUILDKIT = 1

Set-Location (get-item $PSScriptRoot).parent.FullName

docker image pull redis/redis-stack-server:latest
docker image pull trinodb/trino:latest
docker image pull openapitools/openapi-petstore
docker image pull ghcr.io/open-webui/open-webui:main

docker image pull iromu/ai-gateway:latest
docker image pull iromu/mini-chat-ui:latest

docker image pull iromu/mcp-graphql-chat:latest
docker image pull iromu/mcp-server-graphql:latest
docker image pull iromu/graphql-book:latest

docker image pull iromu/mcp-openapi-chat:latest
docker image pull iromu/mcp-server-petstore:latest
docker image pull iromu/mcp-server-samples:latest
docker image pull iromu/ecommerce-booking:latest
docker image pull iromu/ecommerce-product:latest

docker image pull iromu/mcp-server-trino:latest
docker image pull iromu/mcp-trino-chat:latest

docker image pull iromu/rag-redis:latest
docker image pull iromu/rag-redis-advanced:latest

docker image pull iromu/ollama-chat:latest
docker image pull iromu/ollama-chat-advanced:latest

$sw.Stop()
$sw.Elapsed

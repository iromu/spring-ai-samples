$sw = [Diagnostics.Stopwatch]::StartNew()

# To activate custom dockerignore
$env:DOCKER_BUILDKIT = 1

Set-Location (get-item $PSScriptRoot).parent.FullName
Import-Module ./docker/docker_lib.psm1

# UI
dockerBuild 'mini-chat-ui' 'mini-chat-ui'
dockerBuildNative 'ai-gateway' 'ai-gateway'
# MCP GRAPHQL
dockerBuildNative 'mcp/graphql-mcp/mcp-graphql-chat' 'mcp-graphql-chat'
dockerBuildNative 'mcp/graphql-mcp/mcp-server-graphql' 'mcp-server-graphql'
dockerBuildNative 'mcp/graphql-mcp/sample-graphql-services/graphql-book' 'graphql-book'
# MCP OPENAI
dockerBuildNative 'mcp/openapi-mcp/mcp-openapi-chat' 'mcp-openapi-chat'
dockerBuildNative 'mcp/openapi-mcp/mcp-server-petstore' 'mcp-server-petstore'
dockerBuildNative 'mcp/openapi-mcp/mcp-server-samples' 'mcp-server-samples'
dockerBuildNative 'mcp/openapi-mcp/sample-openapi-services/ecommerce-booking' 'ecommerce-booking'
dockerBuildNative 'mcp/openapi-mcp/sample-openapi-services/ecommerce-product' 'ecommerce-product'
# MCP TRINO
dockerBuildNative 'mcp/trino-mcp/mcp-server-trino' 'mcp-server-trino'
dockerBuildNative 'mcp/trino-mcp/mcp-trino-chat' 'mcp-trino-chat'
# MCP RAG
dockerBuildNative 'rag/rag-redis' 'rag-redis'
dockerBuildNative 'rag/rag-redis-advanced' 'rag-redis-advanced'
# CHAT
dockerBuildNative 'chat/ollama-chat' 'ollama-chat'
dockerBuildNative 'chat/ollama-chat-advanced' 'ollama-chat-advanced'

$sw.Stop()

echo "DOCKER FINISHED"
$sw.Elapsed


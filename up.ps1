$sw = [Diagnostics.Stopwatch]::StartNew()

# To activate custom dockerignore
$env:DOCKER_BUILDKIT = 1

docker-compose -f compose.yaml pull ai-gateway
docker-compose -f compose.yaml pull mini-chat-ui
docker-compose -f compose.yaml up -d --remove-orphans

$sw.Stop()
$sw.Elapsed

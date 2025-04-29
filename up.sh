#!/usr/bin/env bash

docker-compose -f compose.yaml pull ai-gateway
docker-compose -f compose.yaml pull mini-chat-ui
docker-compose -f compose.yaml up -d --remove-orphans

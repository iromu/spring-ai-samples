# Stage 1: build
FROM node:20 AS builder
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Stage 2: serve
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy default nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# ENV can be overridden at runtime
ENV VITE_BACKENDS="[]"

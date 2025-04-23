# MCP Server Trino

A reactive metadata query server powered by Spring AI that integrates with [Trino](https://trino.io/) through JDBC
driver. Built on Spring WebFlux and spring-ai-mcp-server frameworks, this server enables asynchronous querying for data
platforms and metadata-driven workloads.

## Features

### Query Capabilities

- Select and filter data from Trino tables using a structured interface
- Execute asynchronous queries with reactive programming model
- Support for complex metadata-driven workloads

### Metadata Operations

- List available Trino catalogs
- Browse schema hierarchies
- Explore table structures
- Retrieve column definitions and properties

## Architecture

The server leverages Spring WebFlux for reactive processing and connects to Trino using the JDBC driver, providing:

- Non-blocking I/O operations
- Reactive streams for data handling
- Integration with Spring AI components

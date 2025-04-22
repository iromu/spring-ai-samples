/*
 * Copyright 2025-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iromu.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for interacting with the Trino metadata system. It retrieves and
 * caches catalogs, schemas, tables, columns, and join information. Uses
 * {@link JdbcTemplate} for SQL execution, and caches results locally using
 * {@link ObjectMapper}.
 *
 * @author Ivan Rodriguez
 */
@Service
@Slf4j
public class TrinoSchemaService {

    private final JdbcTemplate jdbcTemplate;

    public TrinoSchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves all available catalogs from Trino, optionally using a cached version.
     *
     * @return a list of catalog names
     */
    @SneakyThrows
    @Tool(description = "Get catalogs")
    public List<String> getCatalogs() {
        log.info("SHOW CATALOGS");
        List<String> catalogs = jdbcTemplate.queryForList("SHOW CATALOGS", String.class);
        return catalogs;
    }

    /**
     * Retrieves all schemas for a given catalog, optionally using a cached version.
     *
     * @param _catalog the sanitized catalog name
     * @return a list of schema names
     */
    @SneakyThrows
    @Tool(description = "Get schemas for a catalog. Input is a catalog")
    public List<String> getSchemas(String _catalog) {
        log.info("SHOW SCHEMAS FROM {}", _catalog);
        try {
            List<String> schemas = jdbcTemplate
                    .queryForList("SHOW SCHEMAS FROM " + _catalog, String.class);
            return schemas;
        } catch (Exception e) {
            log.error("{} {}", _catalog, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves all tables for a given catalog and schema, optionally using a cached
     * version.
     *
     * @param _catalog the sanitized catalog name
     * @param _schema  the sanitized schema name
     * @return a list of table names
     */
    @SneakyThrows
    @Tool(description = "Get tables for a specific catalog/schema")
    public List<String> getTables(String _catalog, String _schema) {
        log.info("SHOW TABLES FROM {}.{}", _catalog, _schema);
        try {
            List<String> tables = jdbcTemplate.queryForList("SHOW TABLES FROM " + _catalog
                    + "." + _schema, String.class);

            return tables;
        } catch (Exception e) {
            log.error("{}.{} {}", _catalog, _schema, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves column metadata for a specific table, optionally using a cached version.
     *
     * @param _catalog the sanitized catalog name
     * @param _schema  the sanitized schema name
     * @param _table   the sanitized table name
     * @return a list of maps, each representing a column's metadata
     */
    @SneakyThrows
    @Tool(description = "Get columns for a specific catalog/schema/table")
    public List<Map<String, Object>> getColumns(String _catalog, String _schema, String _table) {
        log.info("DESCRIBE {}.{}.{}", _catalog, _schema, _table);
        try {
            List<Map<String, Object>> columns = jdbcTemplate
                    .queryForList("DESCRIBE " + (_catalog) + "."
                            + (_schema) + "." + (_table));

            return columns;
        } catch (Exception e) {
            log.error("{}.{}.{} {}", _catalog, _schema, _table, e.getMessage());
            return new ArrayList<>();
        }
    }

}

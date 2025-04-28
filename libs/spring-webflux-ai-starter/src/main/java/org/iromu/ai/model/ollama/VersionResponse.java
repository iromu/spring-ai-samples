package org.iromu.ai.model.ollama;

/**
 * Represents a response containing version information.
 *
 * This record encapsulates the version string which typically represents the semantic
 * version of the system, application, or component responding.
 */
public record VersionResponse(String version) {
}

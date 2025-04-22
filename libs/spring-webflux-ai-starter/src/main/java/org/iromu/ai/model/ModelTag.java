package org.iromu.ai.model;

public record ModelTag(
        String name,
        String size
) {
    public String getModel() {
        return name;
    }
}

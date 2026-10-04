package dev.chromiumclient.module;

/** Small immutable module definition plus enabled state. */
public final class Module {
    public enum Category { HUD, PVP, VISUAL, UTILITY, PERFORMANCE, WORLD, INTEGRATIONS, SETTINGS }

    public final String name;
    public final String description;
    public final Category category;
    public final boolean integration;
    public boolean enabled;

    public Module(String name, Category category, String description, boolean enabled) {
        this(name, category, description, enabled, false);
    }

    public Module(String name, Category category, String description, boolean enabled, boolean integration) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.enabled = enabled;
        this.integration = integration;
    }

    public void toggle() { if (!integration) enabled = !enabled; }
}

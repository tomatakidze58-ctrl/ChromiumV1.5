package dev.chromiumclient.module;

import dev.chromiumclient.util.Config;
import dev.chromiumclient.util.Reflect;
import dev.chromiumclient.util.ZoomState;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Registers only features that are either implemented in Chromium or are explicit optional integrations. */
public final class ModuleManager {
    private final List<Module> all = new ArrayList<>();

    public ModuleManager() {
        // HUD
        add("FPS Counter", Module.Category.HUD, "Current client FPS", true);
        add("Ping Counter", Module.Category.HUD, "Current multiplayer latency", true);
        add("CPS Counter", Module.Category.HUD, "Left/right clicks per second", false);
        add("Keystrokes", Module.Category.HUD, "WASD, mouse, jump and sneak state", false);
        add("Coordinates", Module.Category.HUD, "Player block coordinates", false);
        add("Direction", Module.Category.HUD, "Cardinal direction and yaw", false);
        add("Speed", Module.Category.HUD, "Horizontal movement speed", false);
        add("Memory", Module.Category.HUD, "JVM memory usage", false);
        add("Clock", Module.Category.HUD, "Local clock", false);
        add("Playtime", Module.Category.HUD, "Current Chromium session time", true);
        add("Server Address", Module.Category.HUD, "Connected server address", false);
        add("Player Count", Module.Category.HUD, "Players visible in the current level", false);
        add("Biome", Module.Category.HUD, "Current biome identifier when available", false);
        add("Food Info", Module.Category.HUD, "Current hunger/saturation/exhaustion info", false);
        add("Locator Bar", Module.Category.HUD, "Compass-style nearby visible-player locator", true);
        add("Waypoint HUD", Module.Category.HUD, "Selected waypoint distance and direction", true);
        add("TNT Countdown", Module.Category.HUD, "Nearest client-known primed TNT fuse timer", false);
        add("Custom Crosshair", Module.Category.HUD, "Chromium crosshair with multiple shapes", true);

        // PvP/QoL
        add("Auto Sprint", Module.Category.PVP, "Always sprint while moving forward when vanilla allows it", true);
        add("Zoom", Module.Category.PVP, "Hold C for smooth client-side FOV zoom", true);
        add("Freelook", Module.Category.PVP, "Hold Left Alt to rotate the camera independently", true);

        // Visual
        add("Fullbright", Module.Category.VISUAL, "Uniform maximum vanilla lightmap brightness", true);
        add("Night Vision", Module.Category.VISUAL, "Client-side bright-view fallback without a server potion", false);
        add("Low Fire", Module.Category.VISUAL, "Lower the first-person fire overlay", true);
        add("Low Shield", Module.Category.VISUAL, "Lower only the shield in first person", true);
        add("No Particles", Module.Category.VISUAL, "Suppress client particle insertion", false);
        add("No Fog", Module.Category.VISUAL, "Push environmental/render fog to the far edge", false);

        // Utility / performance / world
        add("Chat Macros", Module.Category.UTILITY, "F6/F7/F8 send configurable messages", true);
        add("HUD Editor", Module.Category.UTILITY, "Right Ctrl edits Chromium HUD blocks", true);
        add("Dynamic FPS", Module.Category.PERFORMANCE, "Lower FPS cap while the game window is unfocused", false);
        add("FPS Boost", Module.Category.PERFORMANCE, "Reversible preset using real vanilla performance settings", false);
        add("Waypoints", Module.Category.WORLD, "B adds a waypoint and N cycles saved points", true);
        add("Chromium Appearance", Module.Category.SETTINGS, "Global HUD background and Chromium style defaults", true);

        // Optional integrations: status only, never fake toggles.
        integration("AppleSkin", "Official AppleSkin food overlay integration");
        integration("Color Saturation", "Official Color Saturation integration");
        integration("Minimap", "Official compatible minimap integration");
        integration("3D Skin Layers", "Official 3D Skin Layers integration");
        integration("Chat Heads", "Official Chat Heads integration");
        integration("Shulker Tooltip", "Official Shulker Box Tooltip integration");
        integration("Map Tooltip", "Official map preview integration");
        integration("MCTiers Tier Tagger", "Official MCTiers TierTagger integration");
    }

    private void add(String n, Module.Category c, String d, boolean on) { all.add(new Module(n, c, d, on)); }
    private void integration(String n, String d) { all.add(new Module(n, Module.Category.INTEGRATIONS, d, true, true)); }
    public List<Module> all() { return Collections.unmodifiableList(all); }
    public List<Module> in(Module.Category c) { return all.stream().filter(m -> m.category == c).toList(); }
    public Module get(String name) { return all.stream().filter(m -> m.name.equals(name)).findFirst().orElse(null); }
    public boolean on(String name) { Module m = get(name); return m != null && m.enabled && !m.integration; }
    public void toggle(Module m) { if (m != null && !m.integration) { m.toggle(); Config.save(this); } }

    public void tick(Minecraft mc) {
        if (mc == null) return;
        if (mc.player != null && on("Auto Sprint") && Reflect.keyDown(mc.options, "keyUp")) {
            try { mc.player.setSprinting(true); } catch (Throwable ignored) {}
        }
        ZoomState.tick(mc, on("Zoom"));
        dev.chromiumclient.util.PerformanceState.tick(mc, on("Dynamic FPS"), on("FPS Boost"));
    }
}

package dev.chromiumclient.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.chromiumclient.hud.HudLayout;
import dev.chromiumclient.module.Module;
import dev.chromiumclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Fault-tolerant JSON configuration. Corrupt or missing values fall back to defaults. */
public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DIR = FabricLoader.getInstance().getConfigDir().resolve("chromiumclient");
    private static final Path CLIENT = DIR.resolve("client.json");
    private static final Path MODULES = DIR.resolve("modules.json");
    private static final Path HUD = DIR.resolve("hud.json");
    private static final Path MACROS = DIR.resolve("macros.json");

    private Config() {}

    public static void load(ModuleManager modules) {
        try { Files.createDirectories(DIR); } catch (Exception ignored) {}
        loadModules(modules);
        loadClient();
        loadHud();
        loadMacros();
    }

    private static JsonObject read(Path path) {
        if (!Files.exists(path)) return new JsonObject();
        try (Reader r = Files.newBufferedReader(path)) {
            JsonElement e = GSON.fromJson(r, JsonElement.class);
            return e != null && e.isJsonObject() ? e.getAsJsonObject() : new JsonObject();
        } catch (Exception ignored) { return new JsonObject(); }
    }

    private static void loadModules(ModuleManager modules) {
        JsonObject o = read(MODULES);
        for (Module m : modules.all()) if (!m.integration && o.has(m.name)) {
            try { m.enabled = o.get(m.name).getAsBoolean(); } catch (Exception ignored) {}
        }
    }

    private static void loadClient() {
        JsonObject o = read(CLIENT);
        ChromiumSettings.lowFireOffset = f(o, "lowFireOffset", ChromiumSettings.lowFireOffset);
        ChromiumSettings.lowShieldOffset = f(o, "lowShieldOffset", ChromiumSettings.lowShieldOffset);
        ChromiumSettings.freelookSensitivity = f(o, "freelookSensitivity", ChromiumSettings.freelookSensitivity);
        ChromiumSettings.zoomFov = f(o, "zoomFov", ChromiumSettings.zoomFov);
        ChromiumSettings.hudScale = f(o, "hudScale", ChromiumSettings.hudScale);
        ChromiumSettings.hudBackground = b(o, "hudBackground", ChromiumSettings.hudBackground);
        ChromiumSettings.hudStyle = i(o, "hudStyle", ChromiumSettings.hudStyle);
        ChromiumSettings.crosshairStyle = i(o, "crosshairStyle", ChromiumSettings.crosshairStyle);
        ChromiumSettings.crosshairSize = i(o, "crosshairSize", ChromiumSettings.crosshairSize);
        ChromiumSettings.crosshairGap = i(o, "crosshairGap", ChromiumSettings.crosshairGap);
        ChromiumSettings.crosshairThickness = i(o, "crosshairThickness", ChromiumSettings.crosshairThickness);
        ChromiumSettings.crosshairColor = i(o, "crosshairColor", ChromiumSettings.crosshairColor);
        ChromiumSettings.unfocusedFps = i(o, "unfocusedFps", ChromiumSettings.unfocusedFps);
        ChromiumSettings.tntRange = i(o, "tntRange", ChromiumSettings.tntRange);
    }

    private static void loadHud() {
        JsonObject root = read(HUD);
        for (var en : HudLayout.all().entrySet()) {
            if (!root.has(en.getKey()) || !root.get(en.getKey()).isJsonObject()) continue;
            JsonObject o = root.getAsJsonObject(en.getKey());
            HudLayout.Entry e = en.getValue();
            e.x = i(o, "x", e.x); e.y = i(o, "y", e.y);
            e.scale = f(o, "scale", e.scale); e.background = b(o, "background", e.background);
            e.style = i(o, "style", e.style); e.opacity = f(o, "opacity", e.opacity);
        }
    }

    private static void loadMacros() {
        JsonObject o = read(MACROS);
        ChromiumSettings.macro1 = s(o, "macro1", ChromiumSettings.macro1);
        ChromiumSettings.macro2 = s(o, "macro2", ChromiumSettings.macro2);
        ChromiumSettings.macro3 = s(o, "macro3", ChromiumSettings.macro3);
    }

    public static void save(ModuleManager modules) {
        try { Files.createDirectories(DIR); } catch (Exception ignored) {}
        JsonObject mo = new JsonObject();
        for (Module m : modules.all()) if (!m.integration) mo.addProperty(m.name, m.enabled);
        write(MODULES, mo);

        JsonObject c = new JsonObject();
        c.addProperty("lowFireOffset", ChromiumSettings.lowFireOffset);
        c.addProperty("lowShieldOffset", ChromiumSettings.lowShieldOffset);
        c.addProperty("freelookSensitivity", ChromiumSettings.freelookSensitivity);
        c.addProperty("zoomFov", ChromiumSettings.zoomFov);
        c.addProperty("hudScale", ChromiumSettings.hudScale);
        c.addProperty("hudBackground", ChromiumSettings.hudBackground);
        c.addProperty("hudStyle", ChromiumSettings.hudStyle);
        c.addProperty("crosshairStyle", ChromiumSettings.crosshairStyle);
        c.addProperty("crosshairSize", ChromiumSettings.crosshairSize);
        c.addProperty("crosshairGap", ChromiumSettings.crosshairGap);
        c.addProperty("crosshairThickness", ChromiumSettings.crosshairThickness);
        c.addProperty("crosshairColor", ChromiumSettings.crosshairColor);
        c.addProperty("unfocusedFps", ChromiumSettings.unfocusedFps);
        c.addProperty("tntRange", ChromiumSettings.tntRange);
        write(CLIENT, c);

        JsonObject h = new JsonObject();
        for (var en : HudLayout.all().entrySet()) {
            HudLayout.Entry e = en.getValue(); JsonObject o = new JsonObject();
            o.addProperty("x", e.x); o.addProperty("y", e.y); o.addProperty("scale", e.scale);
            o.addProperty("background", e.background); o.addProperty("style", e.style); o.addProperty("opacity", e.opacity);
            h.add(en.getKey(), o);
        }
        write(HUD, h);

        JsonObject ma = new JsonObject();
        ma.addProperty("macro1", ChromiumSettings.macro1); ma.addProperty("macro2", ChromiumSettings.macro2); ma.addProperty("macro3", ChromiumSettings.macro3);
        write(MACROS, ma);
    }

    private static void write(Path path, JsonObject o) {
        try (Writer w = Files.newBufferedWriter(path)) { GSON.toJson(o, w); } catch (Exception ignored) {}
    }
    private static int i(JsonObject o, String k, int f) { try { return o.has(k) ? o.get(k).getAsInt() : f; } catch (Exception e) { return f; } }
    private static float f(JsonObject o, String k, float f) { try { return o.has(k) ? o.get(k).getAsFloat() : f; } catch (Exception e) { return f; } }
    private static boolean b(JsonObject o, String k, boolean f) { try { return o.has(k) ? o.get(k).getAsBoolean() : f; } catch (Exception e) { return f; } }
    private static String s(JsonObject o, String k, String f) { try { return o.has(k) ? o.get(k).getAsString() : f; } catch (Exception e) { return f; } }
}

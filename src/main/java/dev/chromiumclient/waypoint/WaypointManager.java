package dev.chromiumclient.waypoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.chromiumclient.util.Reflect;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Small built-in waypoint fallback. Advanced minimap waypoints are handled by an optional companion mod. */
public final class WaypointManager {
    public record Waypoint(String name, double x, double y, double z, String dimension, int color, boolean visible) {}
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("chromiumclient").resolve("waypoints.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<List<Waypoint>>(){}.getType();
    private static final List<Waypoint> POINTS = new ArrayList<>();
    private static int selected;

    private WaypointManager() {}

    public static void load() {
        POINTS.clear();
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            List<Waypoint> list = GSON.fromJson(r, TYPE);
            if (list != null) POINTS.addAll(list);
        } catch (Exception ignored) {}
        selected = Math.max(0, Math.min(selected, Math.max(0, POINTS.size() - 1)));
    }

    public static void addCurrent(Minecraft mc) {
        if (mc == null || mc.player == null) return;
        String name = "Point " + (POINTS.size() + 1);
        POINTS.add(new Waypoint(name, Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()), dimension(mc), 0xFFC9CDD3, true));
        selected = POINTS.size() - 1; save();
    }

    public static void cycle() { if (!POINTS.isEmpty()) selected = (selected + 1) % POINTS.size(); }
    public static Waypoint selected() { if (POINTS.isEmpty()) return null; selected = Math.max(0, Math.min(selected, POINTS.size()-1)); return POINTS.get(selected); }
    public static List<Waypoint> all() { return List.copyOf(POINTS); }
    public static void removeSelected() { if (POINTS.isEmpty()) return; POINTS.remove(Math.max(0, Math.min(selected, POINTS.size()-1))); selected = Math.max(0, selected-1); save(); }
    public static void renameSelected(String name) { if (POINTS.isEmpty() || name == null || name.isBlank()) return; int i=Math.max(0,Math.min(selected,POINTS.size()-1)); Waypoint w=POINTS.get(i); POINTS.set(i,new Waypoint(name,w.x(),w.y(),w.z(),w.dimension(),w.color(),w.visible())); save(); }

    private static String dimension(Minecraft mc) {
        try {
            Object dim = Reflect.call(mc.level, "dimension");
            Object loc = Reflect.call(dim, "location");
            return loc == null ? String.valueOf(dim) : String.valueOf(loc);
        } catch (Throwable t) { return "unknown"; }
    }

    private static void save() {
        try { Files.createDirectories(FILE.getParent()); try (Writer w = Files.newBufferedWriter(FILE)) { GSON.toJson(POINTS, TYPE, w); } } catch (Exception ignored) {}
    }
}

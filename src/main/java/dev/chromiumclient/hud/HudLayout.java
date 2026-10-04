package dev.chromiumclient.hud;

import java.util.LinkedHashMap;
import java.util.Map;

/** Persistent per-widget layout and style state used by the HUD editor. */
public final class HudLayout {
    public static final class Entry {
        public int x, y, width, height;
        public float scale;
        public boolean background;
        public int style;
        public float opacity;

        public Entry(int x, int y, int width, int height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.scale = 1.0f; this.background = true; this.style = 3; this.opacity = 0.72f;
        }
    }

    private static final Map<String, Entry> ENTRIES = new LinkedHashMap<>();
    static {
        register("stats", 8, 8, 150, 20);
        register("cps", 8, 32, 112, 20);
        register("keys", 8, 56, 112, 58);
        register("coords", 8, 118, 170, 20);
        register("info", 8, 142, 190, 42);
        register("food", 8, 188, 170, 32);
        register("playtime", 8, 224, 118, 20);
        register("waypoint", 8, 248, 190, 22);
        register("locator", 210, 8, 240, 20);
        register("tnt", 210, 32, 150, 20);
    }

    private HudLayout() {}
    private static void register(String id, int x, int y, int w, int h) { ENTRIES.put(id, new Entry(x, y, w, h)); }
    public static Entry get(String id) { return ENTRIES.computeIfAbsent(id, k -> new Entry(8, 8, 120, 20)); }
    public static Map<String, Entry> all() { return ENTRIES; }
    public static void putPosition(String id, int x, int y) { Entry e = get(id); e.x = Math.max(0, x); e.y = Math.max(0, y); }
    public static int scaledWidth(String id) { Entry e = get(id); return Math.max(1, Math.round(e.width * e.scale)); }
    public static int scaledHeight(String id) { Entry e = get(id); return Math.max(1, Math.round(e.height * e.scale)); }
    public static void scale(String id, float delta) { Entry e = get(id); e.scale = Math.max(.50f, Math.min(2.50f, e.scale + delta)); }
    public static void cycleStyle(String id) { Entry e = get(id); e.style = Math.floorMod(e.style + 1, 4); }
    public static void toggleBackground(String id) { Entry e = get(id); e.background = !e.background; }
}

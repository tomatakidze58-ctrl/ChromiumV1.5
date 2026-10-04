package dev.chromiumclient.integration;

import net.fabricmc.loader.api.FabricLoader;

import java.util.LinkedHashMap;
import java.util.Map;

/** Detects optional official companion mods without hard dependencies. */
public final class IntegrationManager {
    public record Integration(String label, String modId, String purpose) {}
    private static final Map<String, Integration> ALL = new LinkedHashMap<>();
    static {
        add("AppleSkin", "appleskin", "Vanilla hunger-bar prediction/food tooltip integration");
        add("Color Saturation", "colorsaturation", "Display color saturation controls");
        add("Minimap", "xaerominimap", "Terrain minimap and advanced waypoint system");
        add("3D Skin Layers", "skinlayers3d", "3D player skin second layers");
        add("Chat Heads", "chat_heads", "Player heads beside chat messages");
        add("Shulker Tooltip", "shulkerboxtooltip", "Preview shulker contents in tooltips");
        add("Map Tooltip", "maptip", "Preview filled maps in tooltips");
        add("MCTiers Tier Tagger", "tiertagger", "MCTiers player-tier display through the installed TierTagger mod");
    }
    private IntegrationManager() {}
    private static void add(String label,String id,String purpose){ALL.put(label,new Integration(label,id,purpose));}
    public static Map<String,Integration> all(){return Map.copyOf(ALL);}
    public static boolean loaded(String label){Integration i=ALL.get(label);return i!=null&&FabricLoader.getInstance().isModLoaded(i.modId());}
    public static String version(String label){
        Integration i=ALL.get(label); if(i==null)return "";
        return FabricLoader.getInstance().getModContainer(i.modId()).map(c->c.getMetadata().getVersion().getFriendlyString()).orElse("");
    }
}

package dev.chromiumclient.hud;

import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.util.ChromiumSettings;
import dev.chromiumclient.util.InputTracker;
import dev.chromiumclient.util.Reflect;
import dev.chromiumclient.waypoint.WaypointManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Original Chromium HUD renderer. It only reads client-known state and never
 * invents hidden entity/server information.
 */
public final class ChromiumHud {
    private static final int TEXT = 0xFFF1F3F5;
    private static final int MUTED = 0xFF9AA1AA;
    private static final int ACCENT = 0xFFD2D5DA;
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");

    private ChromiumHud() {}

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        if (ChromiumClient.MODULES.on("FPS Counter") || ChromiumClient.MODULES.on("Ping Counter"))
            drawAt(g, "stats", e -> drawStats(g, mc, e));
        if (ChromiumClient.MODULES.on("CPS Counter")) drawAt(g, "cps", e -> drawCps(g, mc, e));
        if (ChromiumClient.MODULES.on("Keystrokes")) drawAt(g, "keys", e -> drawKeys(g, mc, e));
        if (ChromiumClient.MODULES.on("Coordinates") || ChromiumClient.MODULES.on("Direction") || ChromiumClient.MODULES.on("Speed"))
            drawAt(g, "coords", e -> drawCoords(g, mc, e));
        if (ChromiumClient.MODULES.on("Memory") || ChromiumClient.MODULES.on("Clock") || ChromiumClient.MODULES.on("Server Address") || ChromiumClient.MODULES.on("Player Count") || ChromiumClient.MODULES.on("Biome"))
            drawAt(g, "info", e -> drawInfo(g, mc, e));
        if (ChromiumClient.MODULES.on("Food Info")) drawAt(g, "food", e -> drawFood(g, mc, e));
        if (ChromiumClient.MODULES.on("Playtime")) drawAt(g, "playtime", e -> drawPlaytime(g, mc, e));
        if (ChromiumClient.MODULES.on("Waypoint HUD")) drawAt(g, "waypoint", e -> drawWaypoint(g, mc, e));
        if (ChromiumClient.MODULES.on("Locator Bar")) drawAt(g, "locator", e -> drawLocator(g, mc, e));
        if (ChromiumClient.MODULES.on("TNT Countdown")) drawAt(g, "tnt", e -> drawTnt(g, mc, e));
        if (ChromiumClient.MODULES.on("Custom Crosshair")) drawCrosshair(g, mc);
    }

    private interface Drawer { void draw(HudLayout.Entry entry); }
    private static void drawAt(GuiGraphicsExtractor g, String id, Drawer draw) {
        HudLayout.Entry e = HudLayout.get(id);
        float s = Math.max(.50f, Math.min(2.50f, e.scale * ChromiumSettings.hudScale));
        g.pose().pushMatrix();
        g.pose().translate(e.x, e.y);
        g.pose().scale(s, s);
        draw.draw(e);
        g.pose().popMatrix();
    }

    private static void panel(GuiGraphicsExtractor g, HudLayout.Entry e, int w, int h) {
        if (!ChromiumClient.MODULES.on("Chromium Appearance") || !(ChromiumSettings.hudBackground && e.background) || e.style == 0 || e.style == 1) return;
        int alpha = Math.max(0x18, Math.min(0xF0, Math.round(e.opacity * 255f)));
        int bg = (alpha << 24) | (e.style == 3 ? 0x101215 : 0x17191D);
        g.fill(0, 0, w, h, bg);
        if (e.style == 3) g.outline(0, 0, w, h, 0x99565C65);
    }

    private static void drawStats(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        String a = ChromiumClient.MODULES.on("FPS Counter") ? "FPS " + fps(mc) : "";
        String b = ChromiumClient.MODULES.on("Ping Counter") ? "PING " + ping(mc) + "ms" : "";
        String text = a + (!a.isEmpty() && !b.isEmpty() ? "   " : "") + b;
        int w = Math.max(92, mc.font.width(text) + 34); panel(g, e, w, 20);
        g.text(mc.font, "Cr", 7, 6, ACCENT, true); g.text(mc.font, text, 26, 6, TEXT, false);
    }

    private static void drawCps(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        String s = "L " + InputTracker.leftCps() + " CPS   R " + InputTracker.rightCps() + " CPS";
        int w = Math.max(108, mc.font.width(s) + 14); panel(g, e, w, 20); g.text(mc.font, s, 7, 6, TEXT, false);
    }

    private static void drawKeys(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        panel(g, e, 112, 58);
        key(g, mc, "W", 38, 4, Reflect.keyDown(mc.options, "keyUp"));
        key(g, mc, "A", 14, 25, Reflect.keyDown(mc.options, "keyLeft"));
        key(g, mc, "S", 38, 25, Reflect.keyDown(mc.options, "keyDown"));
        key(g, mc, "D", 62, 25, Reflect.keyDown(mc.options, "keyRight"));
        key(g, mc, "L", 4, 46, InputTracker.leftHeld());
        key(g, mc, "R", 28, 46, InputTracker.rightHeld());
        key(g, mc, "SP", 52, 46, Reflect.keyDown(mc.options, "keyJump"));
        key(g, mc, "SH", 80, 46, Reflect.keyDown(mc.options, "keyShift"));
    }

    private static void key(GuiGraphicsExtractor g, Minecraft mc, String text, int x, int y, boolean on) {
        int w = text.length() > 1 ? 24 : 20;
        g.fill(x, y, x + w, y + 10, on ? 0xCCD6D9DE : 0x77222529);
        g.outline(x, y, w, 10, on ? 0xFFE6E8EB : 0xFF545A62);
        g.text(mc.font, text, x + 4, y + 2, on ? 0xFF111316 : TEXT, false);
    }

    private static void drawCoords(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        StringBuilder s = new StringBuilder();
        if (ChromiumClient.MODULES.on("Coordinates")) s.append((int)Math.floor(mc.player.getX())).append(" ").append((int)Math.floor(mc.player.getY())).append(" ").append((int)Math.floor(mc.player.getZ()));
        if (ChromiumClient.MODULES.on("Direction")) { if (!s.isEmpty()) s.append("   "); s.append(direction(mc.player.getYRot())); }
        if (ChromiumClient.MODULES.on("Speed")) { if (!s.isEmpty()) s.append("   "); var v = mc.player.getDeltaMovement(); s.append(String.format(Locale.ROOT, "%.2f m/t", Math.sqrt(v.x*v.x + v.z*v.z))); }
        int w = Math.max(140, mc.font.width(s.toString()) + 14); panel(g, e, w, 20); g.text(mc.font, s.toString(), 7, 6, TEXT, false);
    }

    private static void drawInfo(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        String line1 = "", line2 = "";
        if (ChromiumClient.MODULES.on("Memory")) {
            Runtime r = Runtime.getRuntime(); long used = (r.totalMemory() - r.freeMemory()) / (1024L*1024L), max = r.maxMemory() / (1024L*1024L);
            line1 += "RAM " + used + "/" + max + "MB";
        }
        if (ChromiumClient.MODULES.on("Clock")) line1 += (line1.isEmpty()?"":"   ") + LocalTime.now().format(CLOCK);
        if (ChromiumClient.MODULES.on("Player Count") && mc.level != null) line2 += "PLAYERS " + mc.level.players().size();
        if (ChromiumClient.MODULES.on("Server Address")) line2 += (line2.isEmpty()?"":"   ") + server(mc);
        if (ChromiumClient.MODULES.on("Biome")) line2 += (line2.isEmpty()?"":"   ") + biome(mc);
        int w = Math.max(170, Math.max(mc.font.width(line1), mc.font.width(line2)) + 14); panel(g, e, w, 32);
        if (!line1.isEmpty()) g.text(mc.font, line1, 7, 6, TEXT, false);
        if (!line2.isEmpty()) g.text(mc.font, line2, 7, 18, MUTED, false);
    }

    private static void drawFood(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        Object food = Reflect.call(mc.player, "getFoodData");
        int hunger = Reflect.intCall(food, "getFoodLevel", 20);
        Object satObj = Reflect.call(food, "getSaturationLevel");
        float sat = satObj instanceof Number n ? n.floatValue() : 0f;
        Object exObj = Reflect.call(food, "getExhaustionLevel");
        if (!(exObj instanceof Number)) exObj = Reflect.field(food, "exhaustionLevel", "exhaustion");
        float ex = exObj instanceof Number n ? n.floatValue() : 0f;
        String a = "FOOD " + hunger + "/20   SAT " + String.format(Locale.ROOT, "%.1f", sat);
        String b = "EXH " + String.format(Locale.ROOT, "%.2f", ex) + (dev.chromiumclient.integration.IntegrationManager.loaded("AppleSkin") ? "   APPLESKIN ACTIVE" : "");
        int w = Math.max(165, Math.max(mc.font.width(a), mc.font.width(b)) + 14); panel(g, e, w, 32);
        g.text(mc.font, a, 7, 6, TEXT, false); g.text(mc.font, b, 7, 18, MUTED, false);
    }

    private static void drawPlaytime(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        long total = Math.max(0L, (System.currentTimeMillis() - ChromiumClient.SESSION_START) / 1000L);
        long h = total / 3600L, m = (total % 3600L) / 60L, s = total % 60L;
        String time = h > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", h,m,s) : String.format(Locale.ROOT, "%02d:%02d",m,s);
        String text = "PLAY " + time; int w = Math.max(92, mc.font.width(text)+14); panel(g,e,w,20); g.text(mc.font,text,7,6,TEXT,false);
    }

    private static void drawWaypoint(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        WaypointManager.Waypoint wp = WaypointManager.selected(); String line;
        if (wp == null) line = "WAYPOINT none  [B add]";
        else { double dx=wp.x()-mc.player.getX(), dz=wp.z()-mc.player.getZ(); int d=(int)Math.round(Math.sqrt(dx*dx+dz*dz)); line="WAYPOINT "+wp.name()+"  "+d+"m"; }
        int w=Math.max(140,mc.font.width(line)+14); panel(g,e,w,22); g.text(mc.font,line,7,7,TEXT,false);
    }

    private static void drawLocator(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        int w=240,h=20,c=w/2; panel(g,e,w,h); g.fill(c,3,c+1,h-3,ACCENT); g.text(mc.font,"LOC",6,6,MUTED,false);
        if (mc.level==null) return; float yaw=mc.player.getYRot();
        for (Player p:mc.level.players()) { if (p==mc.player) continue; double dx=p.getX()-mc.player.getX(), dz=p.getZ()-mc.player.getZ(); double dist=Math.sqrt(dx*dx+dz*dz); if(dist>96||dist<.01) continue; double angle=Math.toDegrees(Math.atan2(dz,dx))-90.0; double rel=wrap(angle-yaw); if(Math.abs(rel)>90) continue; int x=c+(int)Math.round((rel/90.0)*96.0); g.fill(x-1,5,x+2,15,0xFFE2E5E9); }
    }

    private static void drawTnt(GuiGraphicsExtractor g, Minecraft mc, HudLayout.Entry e) {
        String text = nearestTnt(mc); if (text == null) text = "TNT clear";
        int w=Math.max(110,mc.font.width(text)+14); panel(g,e,w,20); g.text(mc.font,text,7,6,TEXT,false);
    }

    private static String nearestTnt(Minecraft mc) {
        if (mc.level == null) return null;
        Object iterable = Reflect.call(mc.level, "entitiesForRendering");
        if (!(iterable instanceof Iterable<?> it)) return null;
        double best = ChromiumSettings.tntRange * ChromiumSettings.tntRange; Object target = null;
        for (Object entity : it) {
            if (entity == null || !entity.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("tnt")) continue;
            Object x=Reflect.call(entity,"getX"), y=Reflect.call(entity,"getY"), z=Reflect.call(entity,"getZ");
            if (!(x instanceof Number nx) || !(y instanceof Number ny) || !(z instanceof Number nz)) continue;
            double dx=nx.doubleValue()-mc.player.getX(), dy=ny.doubleValue()-mc.player.getY(), dz=nz.doubleValue()-mc.player.getZ(); double d=dx*dx+dy*dy+dz*dz;
            if (d<best) { best=d; target=entity; }
        }
        if (target==null) return null; int fuse=Reflect.intCall(target,"getFuse",-1); return fuse>=0?String.format(Locale.ROOT,"TNT %.2fs",fuse/20.0):"TNT nearby";
    }

    private static void drawCrosshair(GuiGraphicsExtractor g, Minecraft mc) {
        int cx=mc.getWindow().getGuiScaledWidth()/2, cy=mc.getWindow().getGuiScaledHeight()/2;
        int size=Math.max(2,ChromiumSettings.crosshairSize), gap=Math.max(0,ChromiumSettings.crosshairGap), t=Math.max(1,ChromiumSettings.crosshairThickness), c=ChromiumSettings.crosshairColor;
        switch(Math.floorMod(ChromiumSettings.crosshairStyle,7)) {
            case 0 -> cross(g,cx,cy,size,gap,t,c);
            case 1 -> cross(g,cx,cy,size,0,t,c);
            case 2 -> g.fill(cx-t,cy-t,cx+t+1,cy+t+1,c);
            case 3 -> g.outline(cx-size,cy-size,size*2+1,size*2+1,c);
            case 4 -> { cross(g,cx,cy,size,gap,t,c); g.fill(cx-t,cy-t,cx+t+1,cy+t+1,c); }
            case 5 -> { g.fill(cx-size,cy-t,cx+size+1,cy+t+1,c); g.fill(cx-t,cy-size,cx+t+1,cy+1,c); }
            default -> { g.fill(cx-size,cy-t,cx+size+1,cy+t+1,c); g.fill(cx-t,cy-size,cx+t+1,cy+t+1,c); }
        }
    }
    private static void cross(GuiGraphicsExtractor g,int cx,int cy,int s,int gap,int t,int c){ g.fill(cx-t,cy-gap-s,cx+t+1,cy-gap,c); g.fill(cx-t,cy+gap,cx+t+1,cy+gap+s,c); g.fill(cx-gap-s,cy-t,cx-gap,cy+t+1,c); g.fill(cx+gap,cy-t,cx+gap+s,cy+t+1,c); }

    private static int fps(Minecraft mc){ return Reflect.intCall(mc,"getFps",0); }
    private static int ping(Minecraft mc){ Object conn=Reflect.call(mc,"getConnection"); Object info=conn==null?null:Reflect.call(conn,"getPlayerInfo",mc.player.getUUID()); int p=Reflect.intCall(info,"getLatency",-1); if(p<0)p=Reflect.intCall(info,"getPing",0); return Math.max(0,p); }
    private static String direction(float yaw){ int i=Math.floorMod(Math.round(yaw/90f),4); return switch(i){case 0->"S";case 1->"W";case 2->"N";default->"E";}; }
    private static double wrap(double a){ while(a<=-180)a+=360; while(a>180)a-=360; return a; }
    private static String server(Minecraft mc){ Object s=Reflect.call(mc,"getCurrentServer"); Object ip=Reflect.field(s,"ip","address"); return ip==null?"singleplayer":String.valueOf(ip); }
    private static String biome(Minecraft mc){ try{ Object pos=Reflect.call(mc.player,"blockPosition"); Object b=Reflect.call(mc.level,"getBiome",pos); String v=String.valueOf(b); int cut=v.lastIndexOf('/'); if(cut>=0&&cut+1<v.length())v=v.substring(cut+1); return v.length()>28?v.substring(0,28):v; }catch(Throwable t){return "biome";} }
}

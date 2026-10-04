package dev.chromiumclient.gui;

import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.hud.HudLayout;
import dev.chromiumclient.util.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Draggable per-widget HUD editor with independent scale/background/style. */
public final class HudEditorScreen extends Screen {
    private String dragging;
    private String selected = "stats";
    private double grabX, grabY;

    public HudEditorScreen() { super(Component.literal("Chromium HUD Editor")); }

    @Override protected void init() {
        int x = width - 186;
        this.addRenderableWidget(Button.builder(Component.literal("Scale -"), b -> { HudLayout.scale(selected, -.05f); save(); refresh(); }).bounds(x, 74, 78, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Scale +"), b -> { HudLayout.scale(selected, .05f); save(); refresh(); }).bounds(x + 84, 74, 78, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Background"), b -> { HudLayout.toggleBackground(selected); save(); refresh(); }).bounds(x, 102, 162, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Style"), b -> { HudLayout.cycleStyle(selected); save(); refresh(); }).bounds(x, 130, 162, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.minecraft.gui.setScreen(new ChromiumScreen())).bounds(x, height - 34, 162, 22).build());
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        g.fill(0,0,width,height,0xE008090B); g.fill(0,0,width,54,0xFF111317);
        g.centeredText(font,"CHROMIUM HUD EDITOR",width/2,14,0xFFF1F3F5);
        g.centeredText(font,"drag widgets • select to resize/style • 50–250%",width/2,31,0xFF858C95);
        for (var en:HudLayout.all().entrySet()) {
            String id=en.getKey(); HudLayout.Entry e=en.getValue(); int w=HudLayout.scaledWidth(id), h=HudLayout.scaledHeight(id);
            boolean active=id.equals(selected); int bg=active?0xCC353A41:0xAA17191D;
            g.fill(e.x,e.y,e.x+w,e.y+h,bg); g.outline(e.x,e.y,w,h,active?0xFFE7E9EC:0xFF555B63);
            g.text(font,label(id),e.x+6,e.y+6,0xFFE7E9EC,true);
            if(active) g.text(font,Math.round(e.scale*100f)+"%  "+style(e.style)+(e.background?"  BG":"  NO BG"),e.x+6,e.y+18,0xFF9CA3AB,false);
        }
        HudLayout.Entry s=HudLayout.get(selected);
        g.text(font,"SELECTED",width-186,56,0xFF767D86,true); g.text(font,label(selected),width-186,166,0xFFF0F2F4,true);
        g.text(font,Math.round(s.scale*100f)+"% • "+style(s.style),width-186,180,0xFF9BA2AA,false);
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(event.button()==0){
            for(var en:HudLayout.all().entrySet()){
                String id=en.getKey(); HudLayout.Entry e=en.getValue(); int w=HudLayout.scaledWidth(id),h=HudLayout.scaledHeight(id);
                if(event.x()>=e.x&&event.x()<=e.x+w&&event.y()>=e.y&&event.y()<=e.y+h){ selected=id; dragging=id; grabX=event.x()-e.x; grabY=event.y()-e.y; return true; }
            }
        }
        return super.mouseClicked(event,doubleClick);
    }

    @Override public boolean mouseDragged(MouseButtonEvent event,double dx,double dy){
        if(dragging!=null){ HudLayout.Entry e=HudLayout.get(dragging); int w=HudLayout.scaledWidth(dragging),h=HudLayout.scaledHeight(dragging); int nx=(int)Math.round(event.x()-grabX),ny=(int)Math.round(event.y()-grabY); HudLayout.putPosition(dragging,Math.max(0,Math.min(width-w,nx)),Math.max(54,Math.min(height-h,ny))); return true; }
        return super.mouseDragged(event,dx,dy);
    }

    @Override public boolean mouseReleased(MouseButtonEvent event){ if(dragging!=null){dragging=null;save();return true;} return super.mouseReleased(event); }
    private void save(){Config.save(ChromiumClient.MODULES);} private void refresh(){this.minecraft.gui.setScreen(new HudEditorScreen());}
    private static String style(int s){return switch(Math.floorMod(s,4)){case 0->"Vanilla";case 1->"Minimal";case 2->"Clean";default->"Chromium";};}
    private static String label(String id){return switch(id){case"stats"->"FPS / Ping";case"cps"->"CPS";case"keys"->"Keystrokes";case"coords"->"Coordinates";case"info"->"Info";case"food"->"Food";case"playtime"->"Playtime";case"waypoint"->"Waypoint";case"locator"->"Locator";case"tnt"->"TNT";default->id;};}
    @Override public boolean isPauseScreen(){return false;}
}

package dev.chromiumclient.gui;

import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.integration.IntegrationManager;
import dev.chromiumclient.module.Module;
import dev.chromiumclient.util.ChromiumSettings;
import dev.chromiumclient.util.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Original three-column Chromium settings screen. */
public final class ChromiumScreen extends Screen {
    private Module.Category category;
    private String selectedName;

    public ChromiumScreen() { this(Module.Category.HUD, "FPS Counter"); }
    private ChromiumScreen(Module.Category category, String selectedName) {
        super(Component.literal("ChromiumClient"));
        this.category = category;
        this.selectedName = selectedName;
    }

    @Override protected void init() {
        int w=Math.min(1040,width-24), h=Math.min(620,height-24), x=(width-w)/2, y=(height-h)/2;
        int sidebar=158, details=270;

        int cy=y+70;
        for(Module.Category c:Module.Category.values()){
            this.addRenderableWidget(Button.builder(Component.literal((c==category?"● ":"  ")+label(c)), b->
                    this.minecraft.gui.setScreen(new ChromiumScreen(c,first(c))))
                    .bounds(x+12,cy,sidebar-24,23).build());
            cy+=27;
        }

        List<Module> mods=ChromiumClient.MODULES.in(category);
        int listX=x+sidebar+16, listW=w-sidebar-details-44, rowY=y+82;
        for(Module m:mods){
            if(rowY>y+h-44) break;
            int toggleW=56;
            this.addRenderableWidget(Button.builder(Component.literal(m.name), b->
                    this.minecraft.gui.setScreen(new ChromiumScreen(category,m.name)))
                    .bounds(listX,rowY,listW-toggleW-6,25).build());
            if(!m.integration){
                this.addRenderableWidget(Button.builder(Component.literal(m.enabled?"ON":"OFF"), b->{
                    ChromiumClient.MODULES.toggle(m); this.minecraft.gui.setScreen(new ChromiumScreen(category,m.name));
                }).bounds(listX+listW-toggleW,rowY,toggleW,25).build());
            }
            rowY+=31;
        }

        Module s=selected(); if(s==null)return;
        int rx=x+w-details+14, rw=details-28;
        if(!s.integration){
            this.addRenderableWidget(Button.builder(Component.literal(s.enabled?"Disable":"Enable"),b->{ChromiumClient.MODULES.toggle(s);refresh(s.name);})
                    .bounds(rx,y+174,rw,24).build());
        }

        switch(s.name){
            case "Low Fire" -> plusMinus(rx,y+236,rw,s.name,()->ChromiumSettings.lowFireOffset,v->ChromiumSettings.lowFireOffset=clamp(v,0f,.9f),.05f);
            case "Low Shield" -> plusMinus(rx,y+236,rw,s.name,()->ChromiumSettings.lowShieldOffset,v->ChromiumSettings.lowShieldOffset=clamp(v,0f,.8f),.05f);
            case "Freelook" -> plusMinus(rx,y+236,rw,s.name,()->ChromiumSettings.freelookSensitivity,v->ChromiumSettings.freelookSensitivity=clamp(v,.25f,2f),.1f);
            case "Zoom" -> plusMinus(rx,y+236,rw,s.name,()->ChromiumSettings.zoomFov,v->ChromiumSettings.zoomFov=clamp(v,10f,70f),5f);
            case "Dynamic FPS" -> plusMinus(rx,y+236,rw,s.name,()->ChromiumSettings.unfocusedFps,v->ChromiumSettings.unfocusedFps=Math.round(clamp(v,10f,120f)),5f);
            case "TNT Countdown" -> plusMinus(rx,y+236,rw,s.name,()->ChromiumSettings.tntRange,v->ChromiumSettings.tntRange=Math.round(clamp(v,16f,128f)),8f);
            case "Custom Crosshair" -> {
                this.addRenderableWidget(Button.builder(Component.literal("Next crosshair"),b->{ChromiumSettings.crosshairStyle=(ChromiumSettings.crosshairStyle+1)%7;saveRefresh(s.name);}).bounds(rx,y+236,rw,22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Size -"),b->{ChromiumSettings.crosshairSize=Math.max(2,ChromiumSettings.crosshairSize-1);saveRefresh(s.name);}).bounds(rx,y+264,(rw-6)/2,22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Size +"),b->{ChromiumSettings.crosshairSize=Math.min(16,ChromiumSettings.crosshairSize+1);saveRefresh(s.name);}).bounds(rx+(rw+6)/2,y+264,(rw-6)/2,22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Gap -"),b->{ChromiumSettings.crosshairGap=Math.max(0,ChromiumSettings.crosshairGap-1);saveRefresh(s.name);}).bounds(rx,y+292,(rw-6)/2,22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Gap +"),b->{ChromiumSettings.crosshairGap=Math.min(12,ChromiumSettings.crosshairGap+1);saveRefresh(s.name);}).bounds(rx+(rw+6)/2,y+292,(rw-6)/2,22).build());
            }
            case "HUD Editor" -> this.addRenderableWidget(Button.builder(Component.literal("Open HUD editor"),b->this.minecraft.gui.setScreen(new HudEditorScreen())).bounds(rx,y+236,rw,24).build());
            case "Chromium Appearance" -> {
                this.addRenderableWidget(Button.builder(Component.literal("Cycle HUD style"),b->{ChromiumSettings.hudStyle=(ChromiumSettings.hudStyle+1)%4;saveRefresh(s.name);}).bounds(rx,y+236,rw,22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Global background "+(ChromiumSettings.hudBackground?"ON":"OFF")),b->{ChromiumSettings.hudBackground=!ChromiumSettings.hudBackground;saveRefresh(s.name);}).bounds(rx,y+264,rw,22).build());
            }
        }
    }

    private interface FloatGetter{float get();} private interface FloatSetter{void set(float v);}
    private void plusMinus(int x,int y,int w,String module,FloatGetter getter,FloatSetter setter,float step){
        this.addRenderableWidget(Button.builder(Component.literal("-"),b->{setter.set(getter.get()-step);saveRefresh(module);}).bounds(x,y,(w-6)/2,22).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"),b->{setter.set(getter.get()+step);saveRefresh(module);}).bounds(x+(w+6)/2,y,(w-6)/2,22).build());
    }
    private static float clamp(float v,float min,float max){return Math.max(min,Math.min(max,v));}
    private void refresh(String name){this.minecraft.gui.setScreen(new ChromiumScreen(category,name));}
    private void saveRefresh(String name){Config.save(ChromiumClient.MODULES);refresh(name);}

    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta){
        super.extractRenderState(g,mouseX,mouseY,delta);
        int w=Math.min(1040,width-24),h=Math.min(620,height-24),x=(width-w)/2,y=(height-h)/2,sidebar=158,details=270,rightX=x+w-details,listX=x+sidebar+16;
        g.fill(0,0,width,height,0x99000000); g.fill(x,y,x+w,y+h,0xF20A0B0D); g.outline(x,y,w,h,0xFF3B4047);
        g.fill(x,y,x+w,y+54,0xFF111318); g.fill(x,y+54,x+sidebar,y+h,0xFF0D0F12); g.fill(rightX,y+54,x+w,y+h,0xFF0E1013);
        g.fill(x+11,y+12,x+36,y+37,0xFFE0E3E7); g.text(font,"Cr",x+15,y+20,0xFF111317,true);
        g.text(font,"CHROMIUM",x+46,y+17,0xFFF0F2F4,true); g.text(font,"CLIENT 26.2",x+46,y+31,0xFF808790,false); g.text(font,"RIGHT SHIFT",x+w-86,y+23,0xFF777E87,false);
        g.text(font,label(category).toUpperCase(),listX,y+62,0xFFE7E9EC,true); g.text(font,subtitle(category),listX,y+74,0xFF747B84,false);

        Module s=selected(); if(s!=null){ int rx=rightX+14; g.text(font,s.name,rx,y+70,0xFFF2F4F6,true);
            if(s.integration){ boolean loaded=IntegrationManager.loaded(s.name); g.text(font,loaded?"● INSTALLED":"○ NOT INSTALLED",rx,y+91,loaded?0xFFD5D8DD:0xFF777D86,true); drawWrapped(g,s.description,rx,y+114,details-28,0xFF9CA2AA); g.text(font,"Optional companion integration",rx,y+160,0xFF777E87,false); }
            else { g.text(font,s.enabled?"● ENABLED":"○ DISABLED",rx,y+91,s.enabled?0xFFD8DBDF:0xFF737983,true); drawWrapped(g,s.description,rx,y+114,details-28,0xFF9CA2AA); drawSettingValue(g,s,rx,y+212); }
        }
        g.text(font,"C zoom • Left Alt freelook • Right Ctrl HUD • B waypoint • N cycle • F6/F7/F8 macros",x+16,y+h-18,0xFF6D747D,false);
    }

    private void drawSettingValue(GuiGraphicsExtractor g,Module s,int x,int y){
        String value=switch(s.name){
            case "Low Fire"->Math.round(ChromiumSettings.lowFireOffset*100f)+"%";
            case "Low Shield"->Math.round(ChromiumSettings.lowShieldOffset*100f)+"%";
            case "Freelook"->String.format("%.2fx",ChromiumSettings.freelookSensitivity);
            case "Zoom"->Math.round(ChromiumSettings.zoomFov)+" FOV";
            case "Dynamic FPS"->ChromiumSettings.unfocusedFps+" FPS unfocused";
            case "TNT Countdown"->ChromiumSettings.tntRange+" blocks";
            case "Custom Crosshair"->"Style "+(ChromiumSettings.crosshairStyle+1)+" • size "+ChromiumSettings.crosshairSize+" • gap "+ChromiumSettings.crosshairGap;
            case "Chromium Appearance"->"Style "+style(ChromiumSettings.hudStyle)+" • background "+(ChromiumSettings.hudBackground?"on":"off");
            default->"";
        }; if(!value.isEmpty()){g.text(font,"SETTING",x,y,0xFF686F78,true);g.text(font,value,x,y+16,0xFFE4E7EB,true);} }

    private Module selected(){Module m=ChromiumClient.MODULES.get(selectedName);if(m!=null&&m.category==category)return m;List<Module>l=ChromiumClient.MODULES.in(category);return l.isEmpty()?null:l.getFirst();}
    private String first(Module.Category c){List<Module>l=ChromiumClient.MODULES.in(c);return l.isEmpty()?"":l.getFirst().name;}
    private static String label(Module.Category c){return switch(c){case HUD->"HUD";case PVP->"PvP";case VISUAL->"Visual";case UTILITY->"Utility";case PERFORMANCE->"Performance";case WORLD->"World";case INTEGRATIONS->"Integrations";case SETTINGS->"Settings";};}
    private static String subtitle(Module.Category c){return switch(c){case HUD->"compact overlays";case PVP->"legit PvP helpers";case VISUAL->"client-side rendering";case UTILITY->"quality of life";case PERFORMANCE->"reversible vanilla tuning";case WORLD->"waypoints and world HUD";case INTEGRATIONS->"official companion mods";case SETTINGS->"Chromium appearance";};}
    private static String style(int s){return switch(Math.floorMod(s,4)){case 0->"Vanilla";case 1->"Minimal";case 2->"Clean";default->"Chromium";};}
    private void drawWrapped(GuiGraphicsExtractor g,String text,int x,int y,int max,int color){String line="";int yy=y;for(String word:text.split(" ")){String n=line.isEmpty()?word:line+" "+word;if(!line.isEmpty()&&font.width(n)>max){g.text(font,line,x,yy,color,false);yy+=11;line=word;}else line=n;}if(!line.isEmpty())g.text(font,line,x,yy,color,false);}
    @Override public boolean isPauseScreen(){return false;}
}

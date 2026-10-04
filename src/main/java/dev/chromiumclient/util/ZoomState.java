package dev.chromiumclient.util;

import net.minecraft.client.Minecraft;

/** Hold-to-zoom state that always restores the user's previous FOV. */
public final class ZoomState {
    private static boolean held;
    private static Integer originalFov;

    private ZoomState() {}
    public static void setHeld(boolean value) { held = value; }

    public static void tick(Minecraft mc, boolean moduleEnabled) {
        if (mc == null || mc.options == null) return;
        boolean wanted = moduleEnabled && held && mc.gui.screen() == null;
        Object current = Reflect.getOption(mc.options, "fov");
        if (wanted) {
            if (originalFov == null && current instanceof Number n) originalFov = n.intValue();
            Reflect.setOption(mc.options, Math.round(ChromiumSettings.zoomFov), "fov");
        } else if (originalFov != null) {
            Reflect.setOption(mc.options, originalFov, "fov");
            originalFov = null;
        }
    }

    public static void forceRestore(Minecraft mc) {
        if (mc != null && mc.options != null && originalFov != null) Reflect.setOption(mc.options, originalFov, "fov");
        originalFov = null;
        held = false;
    }
}

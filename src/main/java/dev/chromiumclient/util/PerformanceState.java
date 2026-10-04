package dev.chromiumclient.util;

import net.minecraft.client.Minecraft;

/** Reversible performance-state helper. Uses options only when they exist. */
public final class PerformanceState {
    private static Integer originalFps;
    private static Object originalShadows;
    private static boolean boostApplied;

    private PerformanceState() {}

    public static void tick(Minecraft mc, boolean dynamicFps, boolean fpsBoost) {
        if (mc == null || mc.options == null) return;

        if (dynamicFps) {
            Object active = Reflect.call(mc, "isWindowActive");
            boolean focused = !(active instanceof Boolean b) || b;
            Object now = Reflect.getOption(mc.options, "framerateLimit", "maxFps");
            if (!focused) {
                if (originalFps == null && now instanceof Number n) originalFps = n.intValue();
                Reflect.setOption(mc.options, ChromiumSettings.unfocusedFps, "framerateLimit", "maxFps");
            } else if (originalFps != null) {
                Reflect.setOption(mc.options, originalFps, "framerateLimit", "maxFps");
                originalFps = null;
            }
        } else if (originalFps != null) {
            Reflect.setOption(mc.options, originalFps, "framerateLimit", "maxFps");
            originalFps = null;
        }

        if (fpsBoost && !boostApplied) {
            originalShadows = Reflect.getOption(mc.options, "entityShadows");
            if (ChromiumSettings.fpsBoostDisableShadows) Reflect.setOption(mc.options, false, "entityShadows");
            boostApplied = true;
        } else if (!fpsBoost && boostApplied) {
            if (originalShadows != null) Reflect.setOption(mc.options, originalShadows, "entityShadows");
            originalShadows = null;
            boostApplied = false;
        }
    }
}

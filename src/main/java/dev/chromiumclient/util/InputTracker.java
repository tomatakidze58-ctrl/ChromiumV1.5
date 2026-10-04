package dev.chromiumclient.util;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Lightweight click-rate and key-state tracker used only for Chromium HUD telemetry. */
public final class InputTracker {
    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();
    private static boolean lastLeft, lastRight;

    private InputTracker() {}

    public static void tick(Minecraft mc) {
        if (mc == null || mc.getWindow() == null) return;
        long handle;
        try { handle = mc.getWindow().handle(); } catch (Throwable t) { return; }
        boolean left = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (left && !lastLeft) LEFT.addLast(now);
        if (right && !lastRight) RIGHT.addLast(now);
        lastLeft = left;
        lastRight = right;
        trim(LEFT, now); trim(RIGHT, now);
    }

    private static void trim(Deque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000L) q.removeFirst();
    }

    public static int leftCps() { trim(LEFT, System.currentTimeMillis()); return LEFT.size(); }
    public static int rightCps() { trim(RIGHT, System.currentTimeMillis()); return RIGHT.size(); }
    public static boolean leftHeld() { return lastLeft; }
    public static boolean rightHeld() { return lastRight; }
}


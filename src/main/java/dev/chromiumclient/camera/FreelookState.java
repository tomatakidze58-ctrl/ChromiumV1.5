package dev.chromiumclient.camera;

import dev.chromiumclient.ChromiumClient;
import dev.chromiumclient.util.ChromiumSettings;
import net.minecraft.client.Minecraft;

/** Holds camera-only angles while the freelook key is held. */
public final class FreelookState {
    private static boolean active;
    private static float yaw;
    private static float pitch;

    private FreelookState() {}

    public static void updateHeld(boolean held) {
        Minecraft mc = Minecraft.getInstance();
        boolean wanted = held && ChromiumClient.MODULES.on("Freelook") && mc.player != null && mc.gui.screen() == null;
        if (wanted && !active) {
            yaw = mc.player.getYRot();
            pitch = mc.player.getXRot();
            active = true;
        } else if (!wanted && active) {
            active = false;
        }
    }

    public static boolean active() { return active; }
    public static float yaw() { return yaw; }
    public static float pitch() { return pitch; }

    public static void addTurn(float yawDelta, float pitchDelta) {
        if (!active) return;
        float sensitivity = Math.max(0.25f, Math.min(2.0f, ChromiumSettings.freelookSensitivity));
        yaw += yawDelta * sensitivity;
        pitch = Math.max(-90f, Math.min(90f, pitch + pitchDelta * sensitivity));
    }
}

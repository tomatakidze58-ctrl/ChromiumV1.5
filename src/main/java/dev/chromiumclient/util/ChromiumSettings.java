package dev.chromiumclient.util;

/** Runtime settings kept intentionally simple and safe to serialize. */
public final class ChromiumSettings {
    private ChromiumSettings() {}

    public static float lowFireOffset = 0.38f;
    public static float lowShieldOffset = 0.22f;
    public static float freelookSensitivity = 1.0f;
    public static float zoomFov = 30.0f;
    public static float hudScale = 1.0f;
    public static boolean hudBackground = true;
    public static int hudStyle = 3; // 0 vanilla, 1 minimal, 2 clean, 3 chromium
    public static int crosshairStyle = 0;
    public static int crosshairSize = 5;
    public static int crosshairGap = 3;
    public static int crosshairThickness = 1;
    public static int crosshairColor = 0xFFF1F2F4;
    public static int unfocusedFps = 30;
    public static int tntRange = 64;
    public static boolean fpsBoostReduceParticles = true;
    public static boolean fpsBoostDisableShadows = true;
    public static String macro1 = "gg";
    public static String macro2 = "wp";
    public static String macro3 = "Hello!";
}

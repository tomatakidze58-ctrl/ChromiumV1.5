# ChromiumClient feature matrix

## Built into ChromiumClient

| Feature | Status | Notes |
|---|---|---|
| Chromium GUI | Built-in | Right Shift, original three-column gray/black UI |
| HUD editor | Built-in | Right Ctrl, drag + independent 50%-250% scale + style/background |
| FPS / Ping | Built-in | Compact client telemetry |
| CPS | Built-in | 1-second left/right click window |
| Keystrokes | Built-in | WASD, mouse, jump, sneak |
| Coordinates / Direction / Speed | Built-in | Client-known player state |
| RAM / Clock / Session time | Built-in | Local telemetry |
| Server / Player count / Biome | Built-in | Client-known connection/world state |
| Food info | Built-in | Hunger, saturation and exhaustion; AppleSkin can add vanilla-bar prediction |
| Locator bar | Built-in | Only visible/client-known players |
| Waypoints | Built-in | Lightweight fallback, JSON persistence |
| TNT countdown | Built-in | Nearest client-known primed TNT |
| Crosshair | Built-in | Seven shapes, size and gap settings |
| Auto Sprint | Built-in | Uses vanilla sprint state |
| Zoom | Built-in | Hold C; restores previous FOV |
| Freelook | Built-in | Hold Left Alt; camera-only state with rotation restore |
| Fullbright / Night Vision | Built-in | Lightmap-based bright view |
| Low Fire | Built-in | Adjustable overlay offset |
| Low Shield | Built-in | Shield-only first-person transform |
| No Particles | Built-in | Cancels client particle insertion |
| No Fog | Built-in | Adjusts client fog data |
| Chat macros | Built-in | F6/F7/F8, strings stored in macros.json |
| Dynamic FPS | Built-in | Reversible unfocused FPS cap |
| FPS Boost | Built-in | Reversible vanilla option changes |

## Optional integrations

| Feature | Integration |
|---|---|
| AppleSkin prediction/food tooltips | AppleSkin |
| Color saturation | Color Saturation |
| Minimap + advanced waypoints | compatible minimap companion |
| 3D skin layers | 3D Skin Layers |
| Chat player heads | Chat Heads |
| Shulker preview | Shulker Box Tooltip |
| Filled-map preview | Map Tooltip |
| MCTiers labels | Tier Tagger + required library |

Optional mods are deliberately not copied into ChromiumClient. Chromium detects whether they are installed and remains usable when they are absent.

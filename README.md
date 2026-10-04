# ChromiumClient 26.2

ChromiumClient is an original Fabric client project for **Minecraft Java 26.2** with a graphite/black/silver identity and a compact PvP-focused HUD. It is inspired by the usability of modern clients, but does not copy proprietary client code or assets.

## Target toolchain

- Minecraft 26.2
- Java 25
- Fabric Loader 0.19.5
- Fabric API 0.161.0+26.2
- Fabric Loom 1.18-SNAPSHOT
- GitHub Actions: Gradle 9.7.1

## Built-in Chromium features

- Right Shift Chromium module GUI
- Right Ctrl HUD editor
- Per-widget HUD position, scale (50%-250%), background, opacity and style
- HUD styles: Vanilla, Minimal, Clean, Chromium
- FPS counter
- Ping counter
- CPS counter
- Keystrokes
- Coordinates
- Direction
- Horizontal speed
- JVM memory
- Clock
- Session playtime
- Server address
- Player count
- Biome text
- Food/saturation/exhaustion telemetry
- Locator bar using only players already known to the client
- Lightweight built-in waypoints
- Client-known TNT fuse countdown
- Custom crosshair (7 shapes)
- Auto Sprint
- Hold-C Zoom with FOV restore
- Hold-Left-Alt Freelook
- Fullbright / client bright-view mode
- Low Fire
- Low Shield (shield only)
- No Particles
- No Fog
- Chat macros (F6/F7/F8)
- Dynamic FPS
- Reversible FPS Boost preset

## Optional companion integrations

Chromium detects these when their official compatible Fabric mods are installed:

- AppleSkin
- Color Saturation
- Minimap
- 3D Skin Layers
- Chat Heads
- Shulker Box Tooltip
- Map Tooltip
- MCTiers Tier Tagger

The project does **not** copy or rebrand those mods. `install-companions.ps1` asks Modrinth for the newest Fabric 26.2 releases and downloads them directly into `.minecraft/mods`.

## Controls

- **Right Shift** - Chromium GUI
- **Right Ctrl** - HUD editor
- **C** - hold Zoom
- **Left Alt** - hold Freelook
- **B** - add current position as waypoint
- **N** - cycle saved waypoint
- **F6 / F7 / F8** - macros

All key mappings appear in Minecraft's Controls menu and can be rebound.

## Config files

Chromium stores config under:

```text
.minecraft/config/chromiumclient/
```

Files include:

```text
client.json
modules.json
hud.json
waypoints.json
macros.json
```

Missing or malformed values fall back to defaults instead of intentionally crashing the client.

## Build from source

The repo contains a GitHub Actions workflow. Locally you need Java 25 plus a Gradle version compatible with the configured Loom version.

```text
gradle build
```

Use the normal JAR from:

```text
build/libs/ChromiumClient-1.0.0.jar
```

Do not use the `-sources.jar` as the playable mod.

## GitHub build

Push this repository to GitHub. The included `.github/workflows/main.yml` runs the build and uploads `build/libs/*.jar` as the `ChromiumClient` artifact.

## Important testing note

This source was structurally validated in the generation environment (JSON, package layout, mixin references, source-tree checks), but that environment does not provide Java 25 + Gradle + online Fabric/Minecraft dependencies, so a real `gradle build` could not be run here. The GitHub Actions workflow is the final compile check. If it finds a 26.2 API mismatch, use the exact compiler annotation to patch that call rather than deleting the feature.

## Third-party code

Chromium-owned source is MIT licensed. Optional companion mods remain separate projects under their own licenses. See `THIRD-PARTY-NOTICES.md`.

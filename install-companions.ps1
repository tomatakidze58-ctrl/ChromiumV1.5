# ChromiumClient companion-mod installer for Minecraft 26.2 Fabric.
# Downloads official Modrinth releases directly to the user's mods folder.
# It does NOT redistribute third-party jars inside ChromiumClient.

$ErrorActionPreference = "Stop"
$modsDir = Join-Path $env:APPDATA ".minecraft\mods"
New-Item -ItemType Directory -Force -Path $modsDir | Out-Null
$headers = @{ "User-Agent" = "ChromiumClient/1.0 (Minecraft 26.2)" }
$installed = @{}

function Install-Version([object]$version) {
    if ($null -eq $version -or $installed.ContainsKey($version.id)) { return }
    $installed[$version.id] = $true

    foreach ($dep in $version.dependencies) {
        if ($dep.dependency_type -ne "required") { continue }
        try {
            if ($dep.version_id) {
                $dv = Invoke-RestMethod -Headers $headers -Uri ("https://api.modrinth.com/v2/version/" + $dep.version_id)
                Install-Version $dv
            } elseif ($dep.project_id) {
                $url = "https://api.modrinth.com/v2/project/$($dep.project_id)/version?loaders=%5B%22fabric%22%5D&game_versions=%5B%2226.2%22%5D"
                $versions = Invoke-RestMethod -Headers $headers -Uri $url
                $dv = $versions | Sort-Object date_published -Descending | Select-Object -First 1
                Install-Version $dv
            }
        } catch { Write-Host "Dependency lookup warning: $($_.Exception.Message)" -ForegroundColor Yellow }
    }

    $file = $version.files | Where-Object { $_.primary -eq $true } | Select-Object -First 1
    if ($null -eq $file) { $file = $version.files | Select-Object -First 1 }
    if ($null -eq $file) { return }
    $dest = Join-Path $modsDir $file.filename
    Write-Host "Downloading $($file.filename)" -ForegroundColor Cyan
    Invoke-WebRequest -Headers $headers -Uri $file.url -OutFile $dest
}

$projects = @(
    "appleskin",
    "colorsaturation",
    "xaeros-minimap",
    "3dskinlayers",
    "chat-heads",
    "shulkerboxtooltip",
    "maptip",
    "tiertagger",
    "ukulib"
)

foreach ($slug in $projects) {
    try {
        $uri = "https://api.modrinth.com/v2/project/$slug/version?loaders=%5B%22fabric%22%5D&game_versions=%5B%2226.2%22%5D"
        $versions = Invoke-RestMethod -Headers $headers -Uri $uri
        $version = $versions | Where-Object { $_.version_type -eq "release" } | Sort-Object date_published -Descending | Select-Object -First 1
        if ($null -eq $version) { $version = $versions | Sort-Object date_published -Descending | Select-Object -First 1 }
        if ($null -eq $version) { Write-Host "No 26.2 Fabric version found for $slug" -ForegroundColor Yellow; continue }
        Install-Version $version
    } catch {
        Write-Host "Could not install $slug: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "Done. Restart Minecraft after ChromiumClient.jar is also in the mods folder." -ForegroundColor Green

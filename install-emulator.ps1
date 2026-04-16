$ErrorActionPreference = "Stop"

$ANDROID_SDK_ROOT = "$env:USERPROFILE\AndroidSDK"
$CMDLINE_TOOLS_URL = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"

Write-Host "Setting up Android SDK at $ANDROID_SDK_ROOT"

$cmdlineToolsPath = "$ANDROID_SDK_ROOT\cmdline-tools"
$latestPath = "$cmdlineToolsPath\latest"

if (-not (Test-Path $ANDROID_SDK_ROOT)) {
    New-Item -ItemType Directory -Path $ANDROID_SDK_ROOT -Force | Out-Null
}

$zipPath = "$env:TEMP\cmdline-tools.zip"
Write-Host "Downloading command line tools with aria2..."
aria2c.exe -d $env:TEMP -o cmdline-tools.zip "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"

Write-Host "Extracting command line tools..."
if (Test-Path $latestPath) {
    Remove-Item -Recurse -Force $latestPath
}

# Remove old cmdline-tools folder if exists
$extractedFolder = "$cmdlineToolsPath\cmdline-tools"
if (Test-Path $extractedFolder) {
    Remove-Item -Recurse -Force $extractedFolder
}

Expand-Archive -Path $zipPath -DestinationPath $cmdlineToolsPath -Force

# Check what was extracted
$extractedTools = Get-ChildItem $cmdlineToolsPath | Where-Object { $_.Name -ne "latest" } | Select-Object -First 1
if ($extractedTools) {
    Write-Host "Found extracted folder: $($extractedTools.Name)"
    Rename-Item -Path $extractedTools.FullName -NewName "latest" -Force
}

[Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", $ANDROID_SDK_ROOT, "User")
$env:ANDROID_SDK_ROOT = $ANDROID_SDK_ROOT
$env:PATH = "$latestPath\bin;$env:PATH"

Write-Host "Accepting licenses..."
$licensesPath = "$ANDROID_SDK_ROOT\licenses"
if (-not (Test-Path $licensesPath)) {
    New-Item -ItemType Directory -Path $licensesPath -Force | Out-Null
}
"8933bad161af4178b1185d1a37fbf41ea5269c55" | Out-File -FilePath "$licensesPath\android-sdk-license" -Encoding UTF8
"d56f5187479451eabf01fb78af6dfcb131a6481e" | Out-File -FilePath "$licensesPath\android-sdk-license" -Encoding UTF8
"24333f8a63b6825ea9c5514f83c2829b004d1fee" | Out-File -FilePath "$licensesPath\android-sdk-license" -Encoding UTF8
"84831b9409646a918e30573bab4c9c91346d8abd" | Out-File -FilePath "$licensesPath\android-sdk-preview-license" -Encoding UTF8

Write-Host "Installing platform-tools and emulator..."
& "$latestPath\bin\sdkmanager.bat" "platform-tools" "emulator"

Write-Host "Installing system image for Android 16 (API 36)..."
& "$latestPath\bin\sdkmanager.bat" "system-images;android-36;google_apis;arm64-v8a"

Write-Host "Installing build-tools..."
& "$latestPath\bin\sdkmanager.bat" "build-tools;36.0.0"

Write-Host "Installing platform API 36..."
& "$latestPath\bin\sdkmanager.bat" "platforms;android-36"

$emulatorPath = "$ANDROID_SDK_ROOT\emulator"
if ($env:PATH -notlike "*$emulatorPath*") {
    $env:PATH = "$emulatorPath;$env:PATH"
}

Write-Host "Listing available devices..."
& "$latestPath\bin\avdmanager.bat" list devices

Write-Host "Creating Pixel 9 AVD..."
$avdName = "Pixel9"
$avdPath = "$env:USERPROFILE\.android\avd\$avdName.avd"

if (Test-Path $avdPath) {
    Write-Host "Deleting existing AVD..."
    & "$latestPath\bin\avdmanager.bat" delete avd -n $avdName 2>&1 | Out-Null
}

Write-Host "Creating new AVD..."
& "$latestPath\bin\avdmanager.bat" create avd -n $avdName -k "system-images;android-36;google_apis;arm64-v8a" -d "pixel_7" --force

Write-Host "Done! To run the emulator, use:"
Write-Host "emulator -avd Pixel9"

Write-Host "Done! To run the emulator, use:"
Write-Host "emulator -avd Pixel9"
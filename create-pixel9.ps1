$ANDROID_SDK_ROOT = "$env:USERPROFILE\AndroidSDK"
$avdmanager = "$ANDROID_SDK_ROOT\cmdline-tools\latest\bin\avdmanager.bat"

$avdName = "Pixel9"
$avdPath = "$env:USERPROFILE\.android\avd\$avdName.avd"

if (Test-Path $avdPath) {
    Write-Host "Deleting existing AVD..."
    & $avdmanager delete avd -n $avdName
}

Write-Host "Creating Pixel9 AVD..."
echo n | & $avdmanager create avd -n $avdName -k "system-images;android-36;google_apis;arm64-v8a" --force

Write-Host "Configuring Pixel9 settings..."
$configPath = "$avdPath\config.ini"

$configs = @{
    "avd.ini.displayname" = "Pixel 9"
    "hw.lcd.density" = "420"
    "hw.lcd.height" = "2800"
    "hw.lcd.width" = "1280"
    "hw.ramSize" = "8192"
    "hw.accelerometer" = "yes"
    "hw.gpu.enabled" = "yes"
    "hw.gpu.mode" = "auto"
    "hw.keyboard" = "yes"
    "hw.mainKeys" = "no"
    "hw.camera.back" = "virtualscene"
    "hw.camera.front" = "emulated"
    "PlayStore.enabled" = "no"
    "disk.dataPartition.size" = "2G"
}

$configContent = ""
foreach ($key in $configs.Keys) {
    $configContent += "$key=$($configs[$key])`n"
}
$configContent | Out-File -FilePath $configPath -Encoding UTF8

Write-Host "Done! Run: emulator -avd Pixel9"
$keystorePath = "TMessagesProj\config\release.keystore"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$keystoreFullPath = Join-Path $scriptDir $keystorePath
$keystorePassword = Read-Host "Enter keystore password"
$keyAlias = Read-Host "Enter key alias"
$keyPassword = Read-Host "Enter key password"
$validity = 10000
$keytool = "keytool"

$existing = Test-Path $keystoreFullPath
if ($existing) {
    $overwrite = Read-Host "Keystore already exists. Overwrite? (y/n)"
    if ($overwrite -ne "y") {
        Write-Host "Aborted."
        exit
    }
    Remove-Item -Force $keystoreFullPath
}

& $keytool -genkeypair -v -storetype JKS -keyalg RSA -keysize 2048 -validity $validity `
    -keystore "$keystoreFullPath" `
    -storepass $keystorePassword `
    -alias $keyAlias `
    -keypass $keyPassword `
    -dname "CN=Telegram, OU=Developer, O=Telegram, L=Unknown, ST=Unknown, C=US" `
    -ext "SAN=dns:localhost,ip:127.0.0.1"

# Verify keystore was created correctly
& $keytool -list -keystore "$keystoreFullPath" -storepass $keystorePassword

if ($LASTEXITCODE -eq 0) {
    $base64 = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes($keystoreFullPath))
    Write-Host "Keystore created: $keystoreFullPath"
    Write-Host "Base64 encoded:"
    Write-Host $base64
} else {
    Write-Host "Failed to verify keystore"
}
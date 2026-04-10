$keystorePath = "TMessagesProj\config\release.keystore"
$keystorePassword = Read-Host "Enter keystore password"
$keyAlias = Read-Host "Enter key alias"
$keyPassword = Read-Host "Enter key password"
$validity = 10000
$keytool = "keytool"

$existing = Test-Path $keystorePath
if ($existing) {
    $overwrite = Read-Host "Keystore already exists. Overwrite? (y/n)"
    if ($overwrite -ne "y") {
        Write-Host "Aborted."
        exit
    }
}

& $keytool -genkeypair -v -storetype JKS -keyalg RSA -keysize 2048 -validity $validity `
    -keystore $keystorePath `
    -storepass $keystorePassword `
    -alias $keyAlias `
    -keypass $keyPassword `
    -dname "CN=Telegram, OU=Developer, O=Telegram, L=Unknown, ST=Unknown, C=US"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Keystore created: $keystorePath"
} else {
    Write-Host "Failed to create keystore"
}
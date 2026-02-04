<#
.SYNOPSIS
    Builds the Android application using Docker.

.DESCRIPTION
    This script encapsulates the Docker commands required to build the Android application.
    It checks for Docker installation, builds the image, and runs the container.
    The resulting APK is copied to app/build/outputs/apk/debug/.

.EXAMPLE
    .\build_docker.ps1
    Builds the app.

.EXAMPLE
    .\build_docker.ps1 -Clean
    Cleans the docker cache/volumes before building.
#>

param (
    [Switch]$Clean
)

Write-Host "Checking Docker status..." -ForegroundColor Cyan
try {
    docker --version | Out-Null
    docker compose version | Out-Null
}
catch {
    Write-Error "Docker is not found! Please install Docker Desktop for Windows."
    exit 1
}

if ($Clean) {
    Write-Host "Cleaning up old Docker volumes..." -ForegroundColor Yellow
    docker compose down -v
}

Write-Host "Starting Docker Build... (This may take a while)" -ForegroundColor Green
docker compose up --build --exit-code-from android-build

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nBuild Successful!" -ForegroundColor Green
    $apkPath = "app/build/outputs/apk/debug/app-debug.apk"
    Write-Host "APK location: $apkPath" -ForegroundColor Cyan
    
    # Check if file exists
    if (Test-Path $apkPath) {
        # Setup ADB Path
        $adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
        if (-not (Test-Path $adbPath)) {
            Write-Warning "ADB not found at standard location. Cannot install automatically."
            Invoke-Item "app/build/outputs/apk/debug"
        }
        else {
            Write-Host "`nAttempting to install on connected device..." -ForegroundColor Magenta
            
            # Check for devices
            $deviceCheck = & $adbPath devices
            if ($deviceCheck -match "\tdevice") {
                Write-Host "Device found. Installing APK..." -ForegroundColor Green
                & $adbPath install -r $apkPath
                
                Write-Host "Launching App..." -ForegroundColor Green
                # Launch the app (Main Activity)
                & $adbPath shell monkey -p com.bitchat.droid -c android.intent.category.LAUNCHER 1 | Out-Null
                Write-Host "Done! App should be running on your phone." -ForegroundColor Cyan
            }
            else {
                Write-Warning "No device connected via USB Debugging. Skipping installation."
                Invoke-Item "app/build/outputs/apk/debug"
            }
        }
    }
}
else {
    Write-Error "Build Failed! Check the logs above."
}

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
    Write-Host "APK location: app/build/outputs/apk/debug/app-debug.apk" -ForegroundColor Cyan
    
    # Check if file exists
    if (Test-Path "app/build/outputs/apk/debug/app-debug.apk") {
        Invoke-Item "app/build/outputs/apk/debug"
    }
}
else {
    Write-Error "Build Failed! Check the logs above."
}

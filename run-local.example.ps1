Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "         CineBook Web - Local Launcher            " -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Choose Database to connect to:"
Write-Host " [1] Cloud Database (Aiven / Custom)" -ForegroundColor Green
Write-Host " [2] Local MySQL (localhost:3306)" -ForegroundColor Yellow
$choice = Read-Host "Enter choice (1 or 2, default: 1)"

if ($choice -eq "2") {
    Write-Host "`nStarting with Local MySQL (localhost:3306)..." -ForegroundColor Yellow
    $env:DB_URL = "jdbc:mysql://localhost:3306/cinebook_web?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    $env:DB_USER = "root"
    $env:DB_PASSWORD = "root"
} else {
    Write-Host "`nStarting with Cloud Database..." -ForegroundColor Green
    $env:DB_URL = Read-Host "Enter DB_URL"
    $env:DB_USER = Read-Host "Enter DB_USER"
    $env:DB_PASSWORD = Read-Host "Enter DB_PASSWORD"
}

Set-Location "$PSScriptRoot\backend"
Write-Host "`nLaunching Spring Boot on http://localhost:8080 ...`n" -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run


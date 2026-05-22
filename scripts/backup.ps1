param(
    [string]$ContainerName = "school-db",
    [string]$DbName = "schooldb",
    [string]$DbUser = "admin",
    [string]$OutputDir = "backups"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$backupDir = Join-Path $projectRoot $OutputDir

if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir | Out-Null
}

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupFile = Join-Path $backupDir "$DbName`_$timestamp.sql"
$containerBackupFile = "/tmp/$DbName`_$timestamp.sql"

Write-Host "Creating PostgreSQL backup..."
Write-Host "Container: $ContainerName"
Write-Host "Database : $DbName"
Write-Host "Output   : $backupFile"

docker exec $ContainerName sh -c "pg_dump -U $DbUser -d $DbName --clean --if-exists > $containerBackupFile"
if ($LASTEXITCODE -ne 0) {
    throw "pg_dump failed. Check whether Docker is running and container '$ContainerName' exists."
}

docker cp "$ContainerName`:$containerBackupFile" $backupFile
if ($LASTEXITCODE -ne 0) {
    throw "docker cp failed. Backup was created in the container but could not be copied to the host."
}

docker exec $ContainerName rm -f $containerBackupFile | Out-Null

Write-Host "Backup completed: $backupFile"

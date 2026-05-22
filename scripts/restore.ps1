param(
    [Parameter(Mandatory = $true)]
    [string]$BackupFile,

    [string]$ContainerName = "school-db",
    [string]$DbName = "schooldb",
    [string]$DbUser = "admin"
)

$ErrorActionPreference = "Stop"

$resolvedBackupFile = Resolve-Path -Path $BackupFile
$containerRestoreFile = "/tmp/restore_$([Guid]::NewGuid().ToString('N')).sql"

Write-Host "Restoring PostgreSQL backup..."
Write-Host "Container: $ContainerName"
Write-Host "Database : $DbName"
Write-Host "Input    : $resolvedBackupFile"
Write-Host ""
Write-Host "WARNING: restore will overwrite objects included in the backup file."

$confirm = Read-Host "Type RESTORE to continue"
if ($confirm -ne "RESTORE") {
    Write-Host "Restore cancelled."
    exit 0
}

docker cp $resolvedBackupFile "$ContainerName`:$containerRestoreFile"
if ($LASTEXITCODE -ne 0) {
    throw "docker cp failed. Check the backup file and database container."
}

docker exec $ContainerName psql -U $DbUser -d $DbName -v ON_ERROR_STOP=1 -f $containerRestoreFile
if ($LASTEXITCODE -ne 0) {
    docker exec $ContainerName rm -f $containerRestoreFile | Out-Null
    throw "restore failed. Check the backup file and database container."
}

docker exec $ContainerName rm -f $containerRestoreFile | Out-Null

Write-Host "Restore completed."

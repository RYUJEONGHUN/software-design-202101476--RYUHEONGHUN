# Backup and Restore

This project stores PostgreSQL data in the `school-db` Docker container.
Backups are written to the local `backups/` directory and are ignored by Git.

## Backup

```powershell
.\scripts\backup.ps1
```

If PowerShell blocks script execution on Windows, use a one-time bypass:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\backup.ps1
```

Default values:

```text
ContainerName = school-db
DbName        = schooldb
DbUser        = admin
OutputDir     = backups
```

The script creates a timestamped SQL dump:

```text
backups/schooldb_yyyyMMdd_HHmmss.sql
```

## Restore

```powershell
.\scripts\restore.ps1 -BackupFile .\backups\schooldb_yyyyMMdd_HHmmss.sql
```

If script execution is blocked:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\restore.ps1 -BackupFile .\backups\schooldb_yyyyMMdd_HHmmss.sql
```

The restore script asks for `RESTORE` before applying the SQL file because it can overwrite existing database objects.

## Custom Values

```powershell
.\scripts\backup.ps1 -ContainerName school-db -DbName schooldb -DbUser admin
```

```powershell
.\scripts\restore.ps1 -BackupFile .\backups\backup.sql -ContainerName school-db -DbName schooldb -DbUser admin
```

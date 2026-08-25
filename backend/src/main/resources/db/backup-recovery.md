# TradoX Database Backup & Recovery Playbook

This document details operational procedures for MySQL database backup, point-in-time recovery, and secret rotation for TradoX.

---

## 1. Daily Automated Database Backup

```bash
# Export full database schema and data
mysqldump -u root -p --single-transaction --quick --lock-tables=false tradox > tradox_backup_$(date +%Y%m%d_%H%M%S).sql
```

## 2. Point-in-Time Disaster Recovery

```bash
# Create fresh target database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS tradox_restored DEFAULT CHARACTER SET utf8mb4;"

# Restore database snapshot
mysql -u root -p tradox_restored < tradox_backup_20260822_120000.sql
```

## 3. Secret Rotation Strategy
- All stored broker API keys are encrypted via AES-256 in `EncryptionService.java`.
- Rotation requires running database re-encryption utility script during scheduled maintenance window.

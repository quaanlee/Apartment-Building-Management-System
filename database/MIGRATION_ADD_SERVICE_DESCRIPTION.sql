-- ============================================================
-- MIGRATION: Add Description column to Service table
-- Date: 2026-06-26
-- Description: Adds Description column to store service details
-- ============================================================

USE ABMS;
GO

-- Check if Description column already exists
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Service' AND COLUMN_NAME = 'Description'
)
BEGIN
    -- Add Description column to Service table
    ALTER TABLE Service
    ADD Description NVARCHAR(500) NULL;

    PRINT 'SUCCESS: Description column added to Service table';
END
ELSE
BEGIN
    PRINT 'INFO: Description column already exists in Service table';
END
GO


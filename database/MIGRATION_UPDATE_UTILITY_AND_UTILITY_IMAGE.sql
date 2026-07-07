-- ============================================================
-- MIGRATION: Update Utility, UtilityImage, and UtilityPrice Tables
-- Date: 2026-07-07
-- Description:
-- 1. Adds Type column (BIT) to Utility table (0: FREE_USE, 1: RESERVABLE).
-- 2. Changes foreign key in UtilityImage from UtilityID to ResourceID.
-- 3. Changes foreign key in UtilityPrice from UtilityID to ResourceID.
-- ============================================================

USE ABMS;
GO

-- 1. Add Type column to Utility table if it doesn't exist
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Utility' AND COLUMN_NAME = 'Type'
)
BEGIN
    ALTER TABLE Utility
    ADD Type BIT NOT NULL DEFAULT 1;

    PRINT 'SUCCESS: Type column added to Utility table';
END
ELSE
BEGIN
    PRINT 'INFO: Type column already exists in Utility table';
END
GO

-- 2. Update UtilityImage table
-- 2a. Add ResourceID column as NULLABLE first to allow database mapping
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'UtilityImage' AND COLUMN_NAME = 'ResourceID'
)
BEGIN
    ALTER TABLE UtilityImage
    ADD ResourceID INT NULL;
    PRINT 'SUCCESS: Added nullable ResourceID column to UtilityImage';
END
GO

-- 2b. Map existing UtilityImage data to ResourceID based on UtilityID
IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'UtilityImage' AND COLUMN_NAME = 'UtilityID'
)
BEGIN
    EXEC sp_executesql N'
    UPDATE img
    SET img.ResourceID = (SELECT TOP 1 r.ResourceID FROM UtilityResource r WHERE r.UtilityID = img.UtilityID)
    FROM UtilityImage img
    WHERE img.ResourceID IS NULL;
    ';
    PRINT 'SUCCESS: Mapped existing UtilityImage records to ResourceID';
END
GO

-- 2c. Set default ResourceID for any orphan rows if needed (e.g. if no resource exists, use the first overall resource)
DECLARE @FirstResourceID INT;
SELECT TOP 1 @FirstResourceID = ResourceID FROM UtilityResource;
IF @FirstResourceID IS NOT NULL
BEGIN
    UPDATE UtilityImage SET ResourceID = @FirstResourceID WHERE ResourceID IS NULL;
END
GO

-- 2d. Drop old foreign key constraint if exists
IF EXISTS (
    SELECT 1 FROM sys.foreign_keys
    WHERE name = 'FK_UtilityImage_Utility' AND parent_object_id = OBJECT_ID('UtilityImage')
)
BEGIN
    ALTER TABLE UtilityImage
    DROP CONSTRAINT FK_UtilityImage_Utility;
    PRINT 'SUCCESS: Dropped constraint FK_UtilityImage_Utility';
END
GO

-- 2e. Drop UtilityID column if exists
IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'UtilityImage' AND COLUMN_NAME = 'UtilityID'
)
BEGIN
    ALTER TABLE UtilityImage
    DROP COLUMN UtilityID;
    PRINT 'SUCCESS: Dropped column UtilityID from UtilityImage';
END
GO

-- 2f. Alter ResourceID to be NOT NULL now that it is fully populated
ALTER TABLE UtilityImage
ALTER COLUMN ResourceID INT NOT NULL;
PRINT 'SUCCESS: Altered ResourceID column in UtilityImage to NOT NULL';
GO

-- 2g. Add new foreign key constraint if doesn't exist
IF NOT EXISTS (
    SELECT 1 FROM sys.foreign_keys
    WHERE name = 'FK_UtilityImage_UtilityResource' AND parent_object_id = OBJECT_ID('UtilityImage')
)
BEGIN
    ALTER TABLE UtilityImage
    ADD CONSTRAINT FK_UtilityImage_UtilityResource FOREIGN KEY (ResourceID)
    REFERENCES UtilityResource(ResourceID)
    ON DELETE CASCADE;
    PRINT 'SUCCESS: Added constraint FK_UtilityImage_UtilityResource';
END
GO


-- 3. Update UtilityPrice table
-- 3a. Add ResourceID column as NULLABLE first to allow database mapping
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'UtilityPrice' AND COLUMN_NAME = 'ResourceID'
)
BEGIN
    ALTER TABLE UtilityPrice
    ADD ResourceID INT NULL;
    PRINT 'SUCCESS: Added nullable ResourceID column to UtilityPrice';
END
GO

-- 3b. Map existing UtilityPrice data to ResourceID based on UtilityID
IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'UtilityPrice' AND COLUMN_NAME = 'UtilityID'
)
BEGIN
    EXEC sp_executesql N'
    UPDATE p
    SET p.ResourceID = (SELECT TOP 1 r.ResourceID FROM UtilityResource r WHERE r.UtilityID = p.UtilityID)
    FROM UtilityPrice p
    WHERE p.ResourceID IS NULL;
    ';
    PRINT 'SUCCESS: Mapped existing UtilityPrice records to ResourceID';
END
GO

-- 3c. Drop old foreign key constraint if exists
IF EXISTS (
    SELECT 1 FROM sys.foreign_keys
    WHERE name = 'FK_UtilityPrice_Utility' AND parent_object_id = OBJECT_ID('UtilityPrice')
)
BEGIN
    ALTER TABLE UtilityPrice
    DROP CONSTRAINT FK_UtilityPrice_Utility;
    PRINT 'SUCCESS: Dropped constraint FK_UtilityPrice_Utility';
END
GO

-- 3d. Drop UtilityID column if exists
IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'UtilityPrice' AND COLUMN_NAME = 'UtilityID'
)
BEGIN
    ALTER TABLE UtilityPrice
    DROP COLUMN UtilityID;
    PRINT 'SUCCESS: Dropped column UtilityID from UtilityPrice';
END
GO

-- 3e. Alter ResourceID to be NOT NULL now that it is fully populated
ALTER TABLE UtilityPrice
ALTER COLUMN ResourceID INT NOT NULL;
PRINT 'SUCCESS: Altered ResourceID column in UtilityPrice to NOT NULL';
GO

-- 3f. Add new foreign key constraint if doesn't exist
IF NOT EXISTS (
    SELECT 1 FROM sys.foreign_keys
    WHERE name = 'FK_UtilityPrice_UtilityResource' AND parent_object_id = OBJECT_ID('UtilityPrice')
)
BEGIN
    ALTER TABLE UtilityPrice
    ADD CONSTRAINT FK_UtilityPrice_UtilityResource FOREIGN KEY (ResourceID)
    REFERENCES UtilityResource(ResourceID)
    ON DELETE CASCADE;
    PRINT 'SUCCESS: Added constraint FK_UtilityPrice_UtilityResource';
END
GO

-- 4. Add ImageURL to Utility table if not exists
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Utility' AND COLUMN_NAME = 'ImageURL'
)
BEGIN
    ALTER TABLE Utility
    ADD ImageURL NVARCHAR(500) NULL;
    PRINT 'SUCCESS: ImageURL column added to Utility table';
END
GO

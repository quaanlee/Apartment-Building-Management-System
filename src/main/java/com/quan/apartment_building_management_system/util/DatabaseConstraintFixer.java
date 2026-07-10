package com.quan.apartment_building_management_system.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseConstraintFixer {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void fixConstraints() {
        try {
            // Find unique constraints on Profile.CitizenID in SQL Server
            String findProfileConstraintSql = 
                "SELECT tc.CONSTRAINT_NAME " +
                "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
                "JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME " +
                "WHERE tc.CONSTRAINT_TYPE = 'UNIQUE' AND tc.TABLE_NAME = 'Profile' AND kcu.COLUMN_NAME = 'CitizenID'";
            
            @SuppressWarnings("unchecked")
            List<String> profileConstraints = entityManager.createNativeQuery(findProfileConstraintSql).getResultList();
            
            for (String constraintName : profileConstraints) {
                entityManager.createNativeQuery("ALTER TABLE Profile DROP CONSTRAINT " + constraintName).executeUpdate();
                System.out.println("Dropped UNIQUE constraint " + constraintName + " on Profile(CitizenID)");
            }
            
            // Check if filtered index already exists for Profile.CitizenID
            String findProfileIndexSql = 
                "SELECT 1 FROM sys.indexes WHERE name = 'UQ_Profile_CitizenID' AND object_id = OBJECT_ID('Profile')";
            List<?> profileIndexExists = entityManager.createNativeQuery(findProfileIndexSql).getResultList();
            if (profileIndexExists.isEmpty()) {
                entityManager.createNativeQuery(
                    "CREATE UNIQUE NONCLUSTERED INDEX UQ_Profile_CitizenID ON Profile(CitizenID) WHERE CitizenID IS NOT NULL"
                ).executeUpdate();
                System.out.println("Created filtered UNIQUE index UQ_Profile_CitizenID on Profile(CitizenID)");
            }

            // Find unique constraints on Payment.TransactionCode in SQL Server
            String findPaymentConstraintSql = 
                "SELECT tc.CONSTRAINT_NAME " +
                "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
                "JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME " +
                "WHERE tc.CONSTRAINT_TYPE = 'UNIQUE' AND tc.TABLE_NAME = 'Payment' AND kcu.COLUMN_NAME = 'TransactionCode'";
            
            @SuppressWarnings("unchecked")
            List<String> paymentConstraints = entityManager.createNativeQuery(findPaymentConstraintSql).getResultList();
            
            for (String constraintName : paymentConstraints) {
                entityManager.createNativeQuery("ALTER TABLE Payment DROP CONSTRAINT " + constraintName).executeUpdate();
                System.out.println("Dropped UNIQUE constraint " + constraintName + " on Payment(TransactionCode)");
            }
            
            // Check if filtered index already exists for Payment.TransactionCode
            String findPaymentIndexSql = 
                "SELECT 1 FROM sys.indexes WHERE name = 'UQ_Payment_TransactionCode' AND object_id = OBJECT_ID('Payment')";
            List<?> paymentIndexExists = entityManager.createNativeQuery(findPaymentIndexSql).getResultList();
            if (paymentIndexExists.isEmpty()) {
                entityManager.createNativeQuery(
                    "CREATE UNIQUE NONCLUSTERED INDEX UQ_Payment_TransactionCode ON Payment(TransactionCode) WHERE TransactionCode IS NOT NULL"
                ).executeUpdate();
                System.out.println("Created filtered UNIQUE index UQ_Payment_TransactionCode on Payment(TransactionCode)");
            }

            // Drop foreign key constraints and old snake_case tables if they exist
            String[] targetTables = {
                "payment_method", "bill_detail", "resident_apartment", 
                "utility_price", "utility_resource", "utility_booking", 
                "maintenance_request", "maintenance_task", "maintenance_report", 
                "system_log", "service_item"
            };

            for (String targetTable : targetTables) {
                try {
                    String findFKsSql = 
                        "SELECT f.name AS fk_name, OBJECT_SCHEMA_NAME(f.parent_object_id) AS schema_name, OBJECT_NAME(f.parent_object_id) AS table_name " +
                        "FROM sys.foreign_keys AS f " +
                        "WHERE OBJECT_NAME(f.referenced_object_id) = '" + targetTable + "'";
                    
                    @SuppressWarnings("unchecked")
                    List<Object[]> fks = entityManager.createNativeQuery(findFKsSql).getResultList();
                    for (Object[] fk : fks) {
                        String fkName = (String) fk[0];
                        String schemaName = (String) fk[1];
                        String tableName = (String) fk[2];
                        
                        entityManager.createNativeQuery("ALTER TABLE [" + schemaName + "].[" + tableName + "] DROP CONSTRAINT [" + fkName + "]").executeUpdate();
                        System.out.println("Dropped foreign key " + fkName + " on table " + tableName + " referencing " + targetTable);
                    }
                    
                    entityManager.createNativeQuery(
                        "IF OBJECT_ID('dbo." + targetTable + "', 'U') IS NOT NULL " +
                        "DROP TABLE dbo." + targetTable
                    ).executeUpdate();
                } catch (Exception ex) {
                    System.err.println("Warning: Could not clean up old table/constraints for " + targetTable + ": " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not adjust unique nullable constraints: " + e.getMessage());
        }
    }
}

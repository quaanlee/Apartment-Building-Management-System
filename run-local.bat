@echo off
REM Set Cloudinary Environment Variables
set CLOUDINARY_CLOUD_NAME=qvw0oo3c
set CLOUDINARY_API_KEY=468186386315435
set CLOUDINARY_API_SECRET=93KvIuZZmcs_3QhjRnM9XRV8aws

echo Starting Spring Boot with Cloudinary env vars...
echo Cloud Name: %CLOUDINARY_CLOUD_NAME%
echo.

mvnw spring-boot:run

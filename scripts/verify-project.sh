#!/usr/bin/env bash
# ========================================================
# GardenConnect - Project Verification Script (Linux/macOS)
# ========================================================

set -e

echo "=== GardenConnect Build & Verification ==="

# Check Java
if command -v java >/dev/null 2>&1; then
    JAVA_VER=$(java -version 2>&1 | head -n 1)
    echo "[OK] Java runtime detected: $JAVA_VER"
else
    echo "[ERROR] Java 17+ is required but not found on PATH."
    exit 1
fi

# Locate Maven
if command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
elif [ -f "./mvnw" ]; then
    MVN_CMD="./mvnw"
else
    echo "[ERROR] Maven or mvnw not found."
    exit 1
fi

echo "[INFO] Running Maven Clean and Test..."
$MVN_CMD clean test

echo "[INFO] Packaging WAR..."
$MVN_CMD package -DskipTests

if [ -f "target/gardenconnect.war" ]; then
    echo "[SUCCESS] GardenConnect WAR package created successfully at target/gardenconnect.war"
    echo "Ready for deployment to Apache Tomcat 10.1 webapps directory."
else
    echo "[ERROR] WAR package was not found in target/"
    exit 1
fi

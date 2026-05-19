#!/bin/bash
# Quick Start Guide - Frontend-Backend Integration
# This script helps you get everything running quickly

echo "========================================="
echo "Sudo Squad Attendance System - Quick Start"
echo "========================================="
echo ""

# Check prerequisites
echo "[1/5] Checking Prerequisites..."
echo "  - Checking Java..."
java -version 2>&1 | head -3 || echo "⚠️  Java not found!"

echo "  - Checking Maven..."
mvn -v 2>&1 | head -1 || echo "⚠️  Maven not found!"

echo "  - Checking MySQL..."
mysql --version 2>&1 || echo "⚠️  MySQL not found!"

echo ""
echo "[2/5] Building Backend..."
mvn clean install -DskipTests -q
if [ $? -eq 0 ]; then
    echo "✅ Backend built successfully"
else
    echo "❌ Build failed. Check pom.xml and dependencies"
    exit 1
fi

echo ""
echo "[3/5] Starting Backend..."
echo "Backend will run on http://localhost:8080/api/v1"
echo "Press Ctrl+C to stop backend"
mvn spring-boot:run &
BACKEND_PID=$!

sleep 5

echo ""
echo "[4/5] Verifying Backend Health..."
curl -s http://localhost:8080/api/v1/attendance/health | head -c 50
echo ""

echo ""
echo "[5/5] Starting Frontend..."
echo "Frontend available at http://localhost:3000"
echo "Open in browser and verify:"
echo "  - Dashboard displays without errors"
echo "  - 'ESP32 Connection' shows ONLINE"
echo "  - Table is populated"

# Try to start frontend server
if command -v http-server &> /dev/null; then
    npx http-server -p 3000 -c-1
elif command -v python3 &> /dev/null; then
    cd "$(dirname "$0")"
    python3 -m http.server 3000
elif command -v python &> /dev/null; then
    cd "$(dirname "$0")"
    python -m SimpleHTTPServer 3000
else
    echo "No local server found. Open index.html directly:"
    echo "file://$(pwd)/index.html"
fi

# Cleanup on exit
trap "kill $BACKEND_PID" EXIT

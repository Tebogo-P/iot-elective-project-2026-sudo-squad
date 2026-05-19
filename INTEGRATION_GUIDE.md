# Frontend-Backend Integration Guide

## Overview
This document outlines how to run both the frontend and backend together for the Sudo Squad Attendance System.

## Prerequisites
- **Java 21** or higher
- **MySQL Server** running on `localhost:3306`
- **Node.js** (optional, for local development server)
- **Maven** for building the backend

## Backend Setup & Execution

### 1. Database Setup
Ensure MySQL is running and accessible:
```bash
# The application will auto-create the database
# Just ensure MySQL is running with these credentials:
# Host: localhost
# Port: 3306
# Username: admin
# Password: administrator
```

### 2. Build Backend
```bash
cd c:\Users\HP\Documents\iot-elective-project-2026-sudo-squad
mvn clean install
```

### 3. Run Backend
```bash
mvn spring-boot:run
# OR
mvn clean package
java -jar target/SudoSquadAttendanceSystem-0.0.1-SNAPSHOT.jar
```

**Expected Output:**
```
2026-05-19 XX:XX:XX Tomcat initialized with port(s): 8080 (http)
...
Started SudoSquadAttendanceSystemApplication in X.XXX seconds
```

**Backend will be available at:** `http://localhost:8080/api/v1`

## Frontend Setup & Execution

### Option 1: Direct File Opening
1. Open `index.html` in your browser:
   ```
   file:///c:/Users/HP/Documents/iot-elective-project-2026-sudo-squad/index.html
   ```

### Option 2: Local Server (Recommended for Development)
```bash
# Using Python
python -m http.server 3000 --directory c:\Users\HP\Documents\iot-elective-project-2026-sudo-squad

# OR Using Node.js (http-server)
npx http-server c:\Users\HP\Documents\iot-elective-project-2026-sudo-squad -p 3000

# OR Using Live Server in VS Code
# Install Live Server extension, then right-click index.html > Open with Live Server
```

**Frontend will be available at:** `http://localhost:3000`

## API Endpoints Reference

All endpoints are prefixed with `http://localhost:8080/api/v1`

### Attendance Endpoints
- **GET** `/attendance/logs` — Retrieve all attendance logs (used by dashboard)
- **POST** `/attendance/scan` — Process a scan from hardware
- **GET** `/attendance/logs/{id}` — Get single log by ID
- **GET** `/attendance/logs/granted` — All granted access logs
- **GET** `/attendance/logs/denied` — All denied access logs
- **GET** `/attendance/stats` — Get attendance statistics
- **GET** `/attendance/health` — Health check

### Student Endpoints
- **GET** `/students` — Get all students
- **GET** `/students/enrolled` — Get enrolled students only
- **POST** `/students` — Create new student
- **GET** `/students/{id}` — Get student by ID
- **GET** `/students/number/{studentNumber}` — Get student by number
- **PUT** `/students` — Update student
- **PATCH** `/students/{id}/enroll` — Enroll student
- **PATCH** `/students/{id}/unenroll` — Unenroll student
- **DELETE** `/students/{id}` — Delete student

## Integration Testing Checklist

### Step 1: Verify Backend is Running
```bash
curl http://localhost:8080/api/v1/attendance/health
# Expected: "Sudo-Scan backend is running OK"
```

### Step 2: Create Test Data (Backend)
```bash
# Create a test student
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "230226442",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@student.ac.za",
    "rfidTagId": "A1B2C3D4",
    "fingerprintId": 1
  }'
```

### Step 3: Simulate Hardware Scan (Backend)
```bash
# Process a scan from hardware
curl -X POST http://localhost:8080/api/v1/attendance/scan \
  -H "Content-Type: application/json" \
  -d '{
    "nodeId": "MAIN",
    "rfidTag": "A1B2C3D4",
    "fingerprintId": 1
  }'
```

### Step 4: Verify Frontend Displays Data
1. Open frontend at `http://localhost:3000` or `file:///path/to/index.html`
2. Verify that:
   - Dashboard loads without errors
   - "Total Scans" card shows numbers
   - "Present Today" card shows numbers
   - "Access Denied" card shows count
   - "ESP32 Connection" shows "ONLINE"
   - Attendance logs table populates with data

### Step 5: Monitor Network (DevTools)
1. Open Browser DevTools (F12)
2. Go to Network tab
3. Verify requests to `http://localhost:8080/api/v1/attendance/logs` succeed with status 200
4. Check response JSON matches expected format

## Response Format (Frontend Compatibility)

### Attendance Log DTO Response
```json
{
  "id": 1,
  "name": "John Doe",
  "studentNumber": "230226442",
  "timestamp": "2026-05-19 14:30:00",
  "status": "PRESENT",
  "nodeId": "MAIN",
  "rfidTag": "A1B2C3D4"
}
```

## Troubleshooting

### Issue: "Server Offline" message on dashboard
**Possible Causes:**
- Backend not running on port 8080
- MySQL not accessible
- CORS issues

**Solutions:**
1. Verify backend is running: `curl http://localhost:8080/api/v1/attendance/health`
2. Check browser console (F12 > Console) for CORS errors
3. Verify MySQL is running and accessible

### Issue: Empty attendance logs
**Solutions:**
1. Create test data (see Integration Testing Checklist)
2. Simulate a hardware scan
3. Refresh browser

### Issue: Database connection errors
**Solutions:**
1. Verify MySQL is running: `mysql -u admin -p`
2. Verify credentials in `application.properties`
3. Create database manually:
   ```sql
   CREATE DATABASE attendance;
   ```

### Issue: CORS errors in browser console
**Solutions:**
- Backend has `@CrossOrigin(origins = "*")` on all controllers
- If still occurring, check browser console for specific error
- Verify frontend and backend are on different ports (frontend: 3000/file, backend: 8080)

## File Structure
```
iot-elective-project-2026-sudo-squad/
├── index.html              # Frontend dashboard
├── script.js               # Frontend API calls
├── style.css               # Frontend styling
├── pom.xml                 # Backend dependencies
├── src/
│   ├── main/java/.../
│   │   ├── controller/
│   │   │   ├── AttendanceController.java
│   │   │   └── StudentController.java
│   │   ├── domain/
│   │   │   ├── AttendanceLog.java
│   │   │   ├── Student.java
│   │   │   └── HardwarePayload.java
│   │   ├── dto/
│   │   │   └── AttendanceLogDTO.java
│   │   ├── service/
│   │   │   ├── AttendanceService.java
│   │   │   └── StudentService.java
│   │   └── repository/
│   │       ├── AttendanceLogRepository.java
│   │       └── StudentRepository.java
│   └── main/resources/
│       └── application.properties    # Configuration
```

## Configuration Files

### application.properties
```properties
spring.application.name=AttendanceSystem
server.port=8080
server.servlet.context-path=/api/v1

spring.datasource.url=jdbc:mysql://localhost:3306/attendance?createDatabaseIfNotExist=true
spring.datasource.username=admin
spring.datasource.password=administrator

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Development Workflow

### Starting Everything
1. **Terminal 1:** Start MySQL (if not running as service)
2. **Terminal 2:** Start Backend
   ```bash
   mvn spring-boot:run
   ```
3. **Terminal 3:** Start Frontend
   ```bash
   npx http-server -p 3000
   ```
4. Open browser to `http://localhost:3000`

### Next Steps: Hardware Integration
Once frontend-backend is verified working:
1. Deploy ESP32 firmware to hardware node
2. Update `nodeId` in hardware configuration to match backend setup
3. Test with actual RFID and fingerprint sensors
4. Monitor logs in real-time on dashboard

## Success Criteria
- [ ] Backend starts without errors on port 8080
- [ ] Frontend loads without errors  
- [ ] API health check returns 200
- [ ] Test data can be created via API
- [ ] Test scans are logged in database
- [ ] Frontend displays attendance logs
- [ ] CORS headers are properly set
- [ ] Browser console has no errors

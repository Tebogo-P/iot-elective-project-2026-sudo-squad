# API Specification & Communication Protocol

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Currently: None (open API)
Future: JWT tokens recommended

---

## Attendance Endpoints

### 1. Get All Attendance Logs
**Used by:** Frontend Dashboard (every 5 seconds)

**Request:**
```
GET /attendance/logs
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "studentNumber": "230226442",
    "timestamp": "2026-05-19 14:30:00",
    "status": "PRESENT",
    "nodeId": "MAIN",
    "rfidTag": "A1B2C3D4"
  },
  {
    "id": 2,
    "name": "Unknown",
    "studentNumber": "N/A",
    "timestamp": "2026-05-19 14:25:15",
    "status": "DENIED",
    "nodeId": "MAIN",
    "rfidTag": "INVALID_TAG"
  }
]
```

**Error:** `500 Internal Server Error`
```json
{
  "timestamp": "2026-05-19T14:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/v1/attendance/logs"
}
```

---

### 2. Process Hardware Scan
**Used by:** ESP32 Hardware / IoT Device

**Request:**
```
POST /attendance/scan
Content-Type: application/json

{
  "nodeId": "MAIN",
  "rfidTag": "A1B2C3D4",
  "fingerprintId": 1
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "hardwareNodeId": "MAIN",
  "rfidTagId": "A1B2C3D4",
  "fingerprintId": 1,
  "scanTimestamp": "2026-05-19 14:30:00",
  "accessGranted": true,
  "studentName": "John Doe"
}
```

**Logic:**
- If RFID tag OR fingerprint ID matches an enrolled student: `accessGranted = true`
- If neither matches: `accessGranted = false`
- `studentName` is looked up from Student table or set to "Unknown"

---

### 3. Get Single Log
**Request:**
```
GET /attendance/logs/{id}
```

**Response:** `200 OK`
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

**Error:** `404 Not Found`

---

### 4. Get Logs by Node ID
**Request:**
```
GET /attendance/logs/node/{nodeId}
```

**Example:**
```
GET /attendance/logs/node/MAIN
```

**Response:** `200 OK` (array of AttendanceLogDTO)

---

### 5. Get Logs by RFID Tag
**Request:**
```
GET /attendance/logs/rfid/{rfidTag}
```

**Example:**
```
GET /attendance/logs/rfid/A1B2C3D4
```

**Response:** `200 OK` (array of AttendanceLogDTO)

---

### 6. Get Granted Access Logs
**Request:**
```
GET /attendance/logs/granted
```

**Response:** `200 OK` (array of AttendanceLogDTO where status = "PRESENT")

---

### 7. Get Denied Access Logs
**Request:**
```
GET /attendance/logs/denied
```

**Response:** `200 OK` (array of AttendanceLogDTO where status = "DENIED")

---

### 8. Get Logs by Date Range
**Request:**
```
GET /attendance/logs/range?start=2026-05-01T00:00:00&end=2026-05-31T23:59:59
```

**Response:** `200 OK` (array of AttendanceLogDTO)

---

### 9. Get Statistics
**Request:**
```
GET /attendance/stats
```

**Response:** `200 OK`
```json
{
  "totalScans": 42,
  "granted": 38,
  "denied": 4,
  "enrolledStudents": 12
}
```

---

### 10. Delete Log
**Request:**
```
DELETE /attendance/logs/{id}
```

**Response:** `204 No Content`

---

### 11. Health Check
**Request:**
```
GET /attendance/health
```

**Response:** `200 OK`
```
"Sudo-Scan backend is running OK"
```

---

## Student Management Endpoints

### 1. Create Student
**Request:**
```
POST /students
Content-Type: application/json

{
  "studentNumber": "230226442",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@student.ac.za",
  "rfidTagId": "A1B2C3D4",
  "fingerprintId": 1
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "studentNumber": "230226442",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@student.ac.za",
  "rfidTagId": "A1B2C3D4",
  "fingerprintId": 1,
  "enrolled": false
}
```

**Error:** `409 Conflict` (duplicate student number or email)

---

### 2. Get All Students
**Request:**
```
GET /students
```

**Response:** `200 OK` (array of Student objects)

---

### 3. Get Enrolled Students Only
**Request:**
```
GET /students/enrolled
```

**Response:** `200 OK` (array of Student objects where enrolled = true)

---

### 4. Get Student by ID
**Request:**
```
GET /students/{id}
```

**Response:** `200 OK` (Student object)
**Error:** `404 Not Found`

---

### 5. Get Student by Number
**Request:**
```
GET /students/number/{studentNumber}
```

**Response:** `200 OK` (Student object)
**Error:** `404 Not Found`

---

### 6. Update Student
**Request:**
```
PUT /students
Content-Type: application/json

{
  "id": 1,
  "studentNumber": "230226442",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@student.ac.za",
  "rfidTagId": "A1B2C3D4",
  "fingerprintId": 1,
  "enrolled": true
}
```

**Response:** `200 OK` (updated Student object)

---

### 7. Enroll Student
**Request:**
```
PATCH /students/{id}/enroll
```

**Response:** `200 OK` (Student object with enrolled = true)

---

### 8. Unenroll Student
**Request:**
```
PATCH /students/{id}/unenroll
```

**Response:** `200 OK` (Student object with enrolled = false)

---

### 9. Delete Student
**Request:**
```
DELETE /students/{id}
```

**Response:** `204 No Content`

---

## Data Models

### AttendanceLogDTO (Frontend Response)
```typescript
interface AttendanceLogDTO {
  id: number;
  name: string;              // Student name
  studentNumber: string;      // From Student table lookup
  timestamp: string;          // ISO format: "2026-05-19 14:30:00"
  status: string;             // "PRESENT" or "DENIED"
  nodeId: string;             // Hardware node identifier
  rfidTag: string;            // RFID tag used (nullable)
}
```

### AttendanceLog (Internal Domain)
```typescript
interface AttendanceLog {
  id: number;
  hardwareNodeId: string;
  rfidTagId: string;          // Nullable
  fingerprintId: number;      // Nullable
  scanTimestamp: LocalDateTime;
  accessGranted: boolean;
  studentName: string;        // "Unknown" if not found
}
```

### Student
```typescript
interface Student {
  id: number;
  studentNumber: string;      // Unique
  firstName: string;
  lastName: string;
  email: string;              // Unique
  rfidTagId: string;          // Unique, Nullable
  fingerprintId: number;      // Unique, Nullable
  enrolled: boolean;          // Access control flag
}
```

### HardwarePayload (ESP32 Request)
```typescript
interface HardwarePayload {
  nodeId: string;             // "MAIN", "SECONDARY", etc.
  rfidTag: string;            // Nullable
  fingerprintId: number;      // Nullable
}
```

---

## CORS Configuration
All endpoints have CORS enabled:
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Headers: *
```

---

## Status Codes Reference

| Code | Meaning |
|------|---------|
| 200 | OK - Request successful |
| 204 | No Content - Successful deletion |
| 400 | Bad Request - Invalid parameters |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Duplicate data (e.g., student number already exists) |
| 500 | Internal Server Error - Database/Server issue |

---

## Frontend Integration Flow

```
┌─────────────────────────────────────┐
│     Frontend (index.html)            │
│  ┌──────────────────────────────┐   │
│  │ Every 5 seconds:             │   │
│  │ fetch('/api/v1/attendance/logs')  │
│  └───────────┬──────────────────┘   │
│              │                       │
└──────────────┼───────────────────────┘
               │
               │ HTTP GET (CORS)
               ▼
┌─────────────────────────────────────┐
│     Backend (Spring Boot)            │
│  ┌──────────────────────────────┐   │
│  │ AttendanceController         │   │
│  │ GET /attendance/logs         │   │
│  └───────────┬──────────────────┘   │
│              │                       │
│  ┌───────────▼──────────────────┐   │
│  │ AttendanceService            │   │
│  │ getAllLogs()                 │   │
│  │ convertToDTOList()           │   │
│  └───────────┬──────────────────┘   │
│              │                       │
│  ┌───────────▼──────────────────┐   │
│  │ AttendanceLogRepository      │   │
│  │ findAllByOrderByScanDesc()   │   │
│  └───────────┬──────────────────┘   │
│              │                       │
│  ┌───────────▼──────────────────┐   │
│  │ MySQL Database               │   │
│  │ attendance_logs table        │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## Testing with cURL

### Test 1: Health Check
```bash
curl http://localhost:8080/api/v1/attendance/health
```

### Test 2: Get All Logs
```bash
curl http://localhost:8080/api/v1/attendance/logs
```

### Test 3: Create Student
```bash
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

### Test 4: Simulate Scan
```bash
curl -X POST http://localhost:8080/api/v1/attendance/scan \
  -H "Content-Type: application/json" \
  -d '{
    "nodeId": "MAIN",
    "rfidTag": "A1B2C3D4",
    "fingerprintId": 1
  }'
```

### Test 5: Enroll Student
```bash
curl -X PATCH http://localhost:8080/api/v1/students/1/enroll
```

### Test 6: Get Statistics
```bash
curl http://localhost:8080/api/v1/attendance/stats
```

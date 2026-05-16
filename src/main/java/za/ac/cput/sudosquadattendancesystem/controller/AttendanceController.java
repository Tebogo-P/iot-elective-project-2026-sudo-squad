package za.ac.cput.sudosquadattendancesystem.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;
import za.ac.cput.sudosquadattendancesystem.domain.HardwarePayload;
import za.ac.cput.sudosquadattendancesystem.service.IAttendanceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AttendanceController {

    private final IAttendanceService attendanceService;

    @PostMapping("/scan")
    public ResponseEntity<AttendanceLog> processScan(@RequestBody HardwarePayload payload) {
        AttendanceLog result = attendanceService.processScan(payload);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AttendanceLog>> getAllLogs() {
        return ResponseEntity.ok(attendanceService.getAllLogs());
    }

    // GET /api/attendance/logs/1
    @GetMapping("/logs/{id}")
    public ResponseEntity<AttendanceLog> getLogById(@PathVariable Long id) {
        AttendanceLog log = attendanceService.getLogById(id);
        if (log == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(log);
    }

    // GET /api/attendance/logs/node/MAIN
    @GetMapping("/logs/node/{nodeId}")
    public ResponseEntity<List<AttendanceLog>> getByNodeId(@PathVariable String nodeId) {
        return ResponseEntity.ok(attendanceService.getLogsByNodeId(nodeId));
    }

    // GET /api/attendance/logs/rfid/A1B2C3D4
    @GetMapping("/logs/rfid/{rfidTag}")
    public ResponseEntity<List<AttendanceLog>> getByRfidTag(@PathVariable String rfidTag) {
        return ResponseEntity.ok(attendanceService.getLogsByRfidTag(rfidTag));
    }

    // GET /api/attendance/logs/granted   or   /api/attendance/logs/denied
    @GetMapping("/logs/granted")
    public ResponseEntity<List<AttendanceLog>> getGranted() {
        return ResponseEntity.ok(attendanceService.getLogsByAccessGranted(true));
    }

    @GetMapping("/logs/denied")
    public ResponseEntity<List<AttendanceLog>> getDenied() {
        return ResponseEntity.ok(attendanceService.getLogsByAccessGranted(false));
    }

    // GET /api/attendance/logs/range?start=2026-05-01T00:00:00&end=2026-05-15T23:59:59
    @GetMapping("/logs/range")
    public ResponseEntity<List<AttendanceLog>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(attendanceService.getLogsByDateRange(start, end));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(attendanceService.getStats());
    }

    @DeleteMapping("/logs/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        attendanceService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Sudo-Scan backend is running OK");
    }
}


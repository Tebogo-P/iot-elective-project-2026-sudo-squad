package za.ac.cput.sudosquadattendancesystem.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;
import za.ac.cput.sudosquadattendancesystem.domain.HardwarePayload;
import za.ac.cput.sudosquadattendancesystem.dto.AttendanceLogDTO;
import za.ac.cput.sudosquadattendancesystem.service.IAttendanceService;
import za.ac.cput.sudosquadattendancesystem.service.AttendanceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
@RequiredArgsConstructor
public class AttendanceController {

    private final IAttendanceService attendanceService;
    private final AttendanceService attendanceServiceImpl;

    @PostMapping("/scan")
    public ResponseEntity<AttendanceLog> processScan(@RequestBody HardwarePayload payload) {
        AttendanceLog result = attendanceService.processScan(payload);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AttendanceLogDTO>> getAllLogs() {
        List<AttendanceLog> logs = attendanceService.getAllLogs();
        List<AttendanceLogDTO> dtos = attendanceServiceImpl.convertToDTOList(logs);
        return ResponseEntity.ok(dtos);
    }

    // GET /api/v1/attendance/logs/1
    @GetMapping("/logs/{id}")
    public ResponseEntity<AttendanceLogDTO> getLogById(@PathVariable Long id) {
        AttendanceLog log = attendanceService.getLogById(id);
        if (log == null) return ResponseEntity.notFound().build();
        AttendanceLogDTO dto = attendanceServiceImpl.convertToDTO(log);
        return ResponseEntity.ok(dto);
    }

    // GET /api/v1/attendance/logs/node/MAIN
    @GetMapping("/logs/node/{nodeId}")
    public ResponseEntity<List<AttendanceLogDTO>> getByNodeId(@PathVariable String nodeId) {
        List<AttendanceLog> logs = attendanceService.getLogsByNodeId(nodeId);
        List<AttendanceLogDTO> dtos = attendanceServiceImpl.convertToDTOList(logs);
        return ResponseEntity.ok(dtos);
    }

    // GET /api/v1/attendance/logs/rfid/A1B2C3D4
    @GetMapping("/logs/rfid/{rfidTag}")
    public ResponseEntity<List<AttendanceLogDTO>> getByRfidTag(@PathVariable String rfidTag) {
        List<AttendanceLog> logs = attendanceService.getLogsByRfidTag(rfidTag);
        List<AttendanceLogDTO> dtos = attendanceServiceImpl.convertToDTOList(logs);
        return ResponseEntity.ok(dtos);
    }

    // GET /api/v1/attendance/logs/granted or /api/v1/attendance/logs/denied
    @GetMapping("/logs/granted")
    public ResponseEntity<List<AttendanceLogDTO>> getGranted() {
        List<AttendanceLog> logs = attendanceService.getLogsByAccessGranted(true);
        List<AttendanceLogDTO> dtos = attendanceServiceImpl.convertToDTOList(logs);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/logs/denied")
    public ResponseEntity<List<AttendanceLogDTO>> getDenied() {
        List<AttendanceLog> logs = attendanceService.getLogsByAccessGranted(false);
        List<AttendanceLogDTO> dtos = attendanceServiceImpl.convertToDTOList(logs);
        return ResponseEntity.ok(dtos);
    }

    // GET /api/v1/attendance/logs/range?start=2026-05-01T00:00:00&end=2026-05-15T23:59:59
    @GetMapping("/logs/range")
    public ResponseEntity<List<AttendanceLogDTO>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AttendanceLog> logs = attendanceService.getLogsByDateRange(start, end);
        List<AttendanceLogDTO> dtos = attendanceServiceImpl.convertToDTOList(logs);
        return ResponseEntity.ok(dtos);
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



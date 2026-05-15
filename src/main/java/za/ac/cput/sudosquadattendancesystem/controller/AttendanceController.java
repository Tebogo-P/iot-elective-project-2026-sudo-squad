package za.ac.cput.sudosquadattendancesystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.sudosquadattendancesystem.repository.AttendanceLogRepository;
import za.ac.cput.sudosquadattendancesystem.service.AttendanceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceLogRepository attendanceLogRepository;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, AttendanceLogRepository attendanceLogRepository) {
        this.attendanceService = attendanceService;
        this.attendanceLogRepository = attendanceLogRepository;
    }

    /**
     * Endpoint for the ESP32 hardware to send scan data.
     * * @param payload The JSON object sent by the hardware.
     * @return HTTP 200 OK if granted, HTTP 403 FORBIDDEN if denied.
     */
    @PostMapping("/log")
    public ResponseEntity<String> logAttendance(@RequestBody HardwarePayloadDTO payload) {
        try {
            AttendanceLog savedLog = attendanceService.recordScan(payload);

            if (savedLog.isAccessGranted()) {
                return ResponseEntity.ok("ACCESS_GRANTED");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ACCESS_DENIED");
            }
        } catch (Exception e) {
            // Log the error for debugging purposes
            System.err.println("Error processing hardware scan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SERVER_ERROR");
        }
    }

    /**
     * Endpoint for the Frontend Web Dashboard to retrieve the logs.
     * * @return A list of all attendance logs.
     */
    @GetMapping("/logs")
    public ResponseEntity<List<AttendanceLog>> getAllLogs() {
        // Retrieves all logs from the database.
        // In a production environment, this should be paginated!
        List<AttendanceLog> logs = attendanceLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}

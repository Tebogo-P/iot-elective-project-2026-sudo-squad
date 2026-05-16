package za.ac.cput.sudosquadattendancesystem.service;

import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;
import za.ac.cput.sudosquadattendancesystem.domain.HardwarePayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IAttendanceService {
    // Called by the ESP32 for every scan
    AttendanceLog processScan(HardwarePayload payload);

    // Fetch methods for the dashboard and reports
    List<AttendanceLog> getAllLogs();
    AttendanceLog       getLogById(Long id);
    List<AttendanceLog> getLogsByNodeId(String nodeId);
    List<AttendanceLog> getLogsByRfidTag(String rfidTag);
    List<AttendanceLog> getLogsByAccessGranted(boolean accessGranted);
    List<AttendanceLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end);

    // Summary numbers for the dashboard stats cards
    Map<String, Long> getStats();

    // Admin: remove a bad/test log entry
    void deleteLog(Long id);
}

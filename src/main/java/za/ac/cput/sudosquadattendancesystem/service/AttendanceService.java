package za.ac.cput.sudosquadattendancesystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;
import za.ac.cput.sudosquadattendancesystem.domain.HardwarePayload;
import za.ac.cput.sudosquadattendancesystem.domain.Student;
import za.ac.cput.sudosquadattendancesystem.dto.AttendanceLogDTO;
import za.ac.cput.sudosquadattendancesystem.repository.AttendanceLogRepository;
import za.ac.cput.sudosquadattendancesystem.repository.StudentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService implements IAttendanceService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final StudentRepository       studentRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public AttendanceLog processScan(HardwarePayload payload) {

        log.info("Scan received — node: '{}', RFID: '{}', Fingerprint: {}",
                payload.getNodeId(), payload.getRfidTag(), payload.getFingerprintId());

        String studentName = resolveStudentName(payload.getRfidTag(), payload.getFingerprintId());

        boolean isAuthorised = validateCredentials(payload.getRfidTag(), payload.getFingerprintId());

        AttendanceLog logEntry = AttendanceLog.builder()
                .hardwareNodeId(payload.getNodeId())
                .rfidTagId(payload.getRfidTag())
                .fingerprintId(payload.getFingerprintId())
                .scanTimestamp(LocalDateTime.now())
                .accessGranted(isAuthorised)
                .studentName(studentName)
                .build();

        AttendanceLog saved = attendanceLogRepository.save(logEntry);

        log.info("Scan logged (id={}) student='{}' — outcome: {}",
                saved.getId(), studentName, isAuthorised ? "ACCESS_GRANTED" : "ACCESS_DENIED");

        return saved;
    }

    @Override
    public List<AttendanceLog> getAllLogs() {
        return attendanceLogRepository.findAllByOrderByScanTimestampDesc();
    }

    @Override
    public AttendanceLog getLogById(Long id) {
        return attendanceLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<AttendanceLog> getLogsByNodeId(String nodeId) {
        return attendanceLogRepository.findByHardwareNodeIdOrderByScanTimestampDesc(nodeId);
    }

    @Override
    public List<AttendanceLog> getLogsByRfidTag(String rfidTag) {
        return attendanceLogRepository.findByRfidTagIdOrderByScanTimestampDesc(rfidTag);
    }

    @Override
    public List<AttendanceLog> getLogsByAccessGranted(boolean accessGranted) {
        return attendanceLogRepository.findByAccessGrantedOrderByScanTimestampDesc(accessGranted);
    }

    @Override
    public List<AttendanceLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return attendanceLogRepository.findByScanTimestampBetweenOrderByScanTimestampDesc(start, end);
    }

    @Override
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("totalScans", attendanceLogRepository.count());
        stats.put("granted", attendanceLogRepository.countByAccessGranted(true));
        stats.put("denied", attendanceLogRepository.countByAccessGranted(false));
        stats.put("enrolledStudents", studentRepository.count());
        return stats;
    }

    @Override
    public void deleteLog(Long id) {
        attendanceLogRepository.deleteById(id);
        log.info("Log entry {} deleted", id);
    }
    
    /**
     * Convert AttendanceLog domain object to AttendanceLogDTO for frontend compatibility.
     * Maps internal field names to frontend-expected field names.
     */
    public AttendanceLogDTO convertToDTO(AttendanceLog log) {
        String studentNumber = "N/A";
        
        // Try to find student number from RFID tag
        if (log.getRfidTagId() != null) {
            Optional<Student> student = studentRepository.findByRfidTagId(log.getRfidTagId());
            if (student.isPresent()) {
                studentNumber = student.get().getStudentNumber();
            }
        } 
        // Try to find student number from fingerprint ID
        else if (log.getFingerprintId() != null) {
            Optional<Student> student = studentRepository.findByFingerprintId(log.getFingerprintId());
            if (student.isPresent()) {
                studentNumber = student.get().getStudentNumber();
            }
        }
        
        // Determine status
        String status = log.isAccessGranted() ? "PRESENT" : "DENIED";
        
        return AttendanceLogDTO.builder()
                .id(log.getId())
                .name(log.getStudentName())
                .studentNumber(studentNumber)
                .timestamp(log.getScanTimestamp().format(DATE_FORMATTER))
                .status(status)
                .nodeId(log.getHardwareNodeId())
                .rfidTag(log.getRfidTagId())
                .build();
    }
    
    /**
     * Convert list of AttendanceLog objects to DTOs.
     */
    public List<AttendanceLogDTO> convertToDTOList(List<AttendanceLog> logs) {
        return logs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private boolean validateCredentials(String rfidTag, Integer fingerprintId) {
        boolean rfidValid = rfidTag != null && studentRepository.existsByRfidTagIdAndEnrolledTrue(rfidTag);
        boolean fingerprintValid = fingerprintId != null && studentRepository.existsByFingerprintIdAndEnrolledTrue(fingerprintId);
        return rfidValid || fingerprintValid;
    }

    private String resolveStudentName(String rfidTag, Integer fingerprintId) {
        if (rfidTag != null) {
            Optional<Student> byRfid = studentRepository.findByRfidTagId(rfidTag);
            if (byRfid.isPresent()) {
                Student s = byRfid.get();
                return s.getFirstName() + " " + s.getLastName();
            }
        }
        if (fingerprintId != null) {
            Optional<Student> byFp = studentRepository.findByFingerprintId(fingerprintId);
            if (byFp.isPresent()) {
                Student s = byFp.get();
                return s.getFirstName() + " " + s.getLastName();
            }
        }
        return "Unknown";
    }
}
package za.ac.cput.sudosquadattendancesystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;
import za.ac.cput.sudosquadattendancesystem.domain.HardwarePayload;
import za.ac.cput.sudosquadattendancesystem.domain.Student;
import za.ac.cput.sudosquadattendancesystem.repository.AttendanceLogRepository;
import za.ac.cput.sudosquadattendancesystem.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService implements IAttendanceService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final StudentRepository       studentRepository;

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
     * Checks the student table for an enrolled record that matches either
     * the RFID tag or fingerprint ID. This is dynamic — adding a new student
     * to the DB automatically grants them access without a code change.
     */
    private boolean validateCredentials(String rfidTag, Integer fingerprintId) {
        boolean rfidValid        = rfidTag != null       && studentRepository.existsByRfidTagIdAndEnrolledTrue(rfidTag);
        boolean fingerprintValid = fingerprintId != null && studentRepository.existsByFingerprintIdAndEnrolledTrue(fingerprintId);
        return rfidValid || fingerprintValid;
    }

    /**
     * Tries to find a student name to embed in the log row.
     * Returns "Unknown" for unrecognised cards — useful for spotting intruders.
     */
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
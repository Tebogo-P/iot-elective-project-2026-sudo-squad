package za.ac.cput.sudosquadattendancesystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;
import za.ac.cput.sudosquadattendancesystem.repository.AttendanceLogRepository;

import java.time.LocalDateTime;

/**
 * Core business logic service for the Sudo Squad Attendance System.
 * * This class acts as the middle layer between the REST Controller and the Database Repository.
 * It is responsible for parsing incoming hardware payloads, validating credentials,
 * and preparing the data entity for persistent storage.
 */
@Service
public class AttendanceService {

    private final AttendanceLogRepository attendanceLogRepository;

    /**
     * Constructor-based dependency injection.
     * This is preferred over @Autowired on the field directly as it makes the class easier to unit test.
     */
    @Autowired
    public AttendanceService(AttendanceLogRepository attendanceLogRepository) {
        this.attendanceLogRepository = attendanceLogRepository;
    }

    /**
     * Processes the incoming hardware scan and logs it to the database.
     * * @Transactional is used to ensure database integrity. If anything fails during the save process,
     * the entire transaction rolls back, preventing partial or corrupted data entries.
     *
     * @param payload The Data Transfer Object received from the ESP32.
     * @return The fully constructed and saved AttendanceLog entity.
     */
    @Transactional
    public AttendanceLog recordScan(HardwarePayloadDTO payload) {

        boolean isAuthorized = validateCredentials(payload.getRfidTag(), payload.getFingerprintId());

        AttendanceLog logEntry = AttendanceLog.builder()
                .hardwareNodeId(payload.getNodeId())
                .rfidTagId(payload.getRfidTag())
                .fingerprintId(payload.getFingerprintId())
                .scanTimestamp(LocalDateTime.now())
                .accessGranted(isAuthorized)
                .build();

        return attendanceLogRepository.save(logEntry);
    }

    /**
     * Validates the provided RFID or Fingerprint ID against known records.
     * * ACADEMIC NOTE: This currently utilizes mock data to allow the frontend and hardware teams
     * to test the integration (Phase 1/2) without waiting for the full DBA implementation.
     * In the final enterprise build, this will query a dedicated 'Users' database table.
     *
     * @param rfid The RFID tag string from the RC522.
     * @param fingerprintId The integer ID from the AS608.
     * @return true if credentials match known valid IDs, false otherwise.
     */
    private boolean validateCredentials(String rfid, Integer fingerprintId) {
        String validTag1 = "A1B2C3D4";
        String validTag2 = "FF99EE88";

        Integer validFingerprint1 = 1;
        Integer validFingerprint2 = 2;

        if (rfid != null && !rfid.trim().isEmpty()) {
            if (rfid.equalsIgnoreCase(validTag1) || rfid.equalsIgnoreCase(validTag2)) {
                return true;
            }
        }

        if (fingerprintId != null) {
            if (fingerprintId.equals(validFingerprint1) || fingerprintId.equals(validFingerprint2)) {
                return true;
            }
        }

        return false;
    }
}

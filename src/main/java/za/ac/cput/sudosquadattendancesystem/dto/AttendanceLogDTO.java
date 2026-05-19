package za.ac.cput.sudosquadattendancesystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Attendance Log response to frontend.
 * Maps internal AttendanceLog domain to frontend-compatible field names.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLogDTO {

    private Long id;

    /**
     * Student name (from AttendanceLog.studentName)
     */
    private String name;

    /**
     * Student number (fetched from Student entity if available)
     */
    private String studentNumber;

    /**
     * Timestamp of the scan
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    /**
     * Access status: PRESENT, LATE, or DENIED
     */
    private String status;

    /**
     * Node ID for device tracking
     */
    private String nodeId;

    /**
     * RFID tag identifier
     */
    private String rfidTag;
}

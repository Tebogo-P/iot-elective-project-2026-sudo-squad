package za.ac.cput.sudosquadattendancesystem.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Table(name = "attendance_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hardware_node_id", nullable = false)
    private String hardwareNodeId;

    @Column(name = "rfid_tag_id")
    private String rfidTagId;

    @Column(name = "fingerprint_id")
    private Integer fingerprintId;

    @Column(name = "scan_timestamp", nullable = false)
    private LocalDateTime scanTimestamp;

    @Column(name = "access_granted", nullable = false)
    private boolean accessGranted;
}

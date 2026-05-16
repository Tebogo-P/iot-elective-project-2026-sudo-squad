package za.ac.cput.sudosquadattendancesystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.sudosquadattendancesystem.domain.AttendanceLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {

    List<AttendanceLog> findAllByOrderByScanTimestampDesc();

    List<AttendanceLog> findByHardwareNodeIdOrderByScanTimestampDesc(String hardwareNodeId);

    List<AttendanceLog> findByRfidTagIdOrderByScanTimestampDesc(String rfidTagId);

    List<AttendanceLog> findByAccessGrantedOrderByScanTimestampDesc(boolean accessGranted);

    List<AttendanceLog> findByScanTimestampBetweenOrderByScanTimestampDesc(
            LocalDateTime start, LocalDateTime end);

    long countByAccessGranted(boolean accessGranted);

    long countByHardwareNodeId(String hardwareNodeId);
}

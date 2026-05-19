package za.ac.cput.sudosquadattendancesystem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLogDTO {

    private Long id;

    private String name;

    private String studentNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    private String status;

    private String nodeId;

    private String rfidTag;
}

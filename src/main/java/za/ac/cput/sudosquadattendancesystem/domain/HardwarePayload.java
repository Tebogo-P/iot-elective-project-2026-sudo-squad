package za.ac.cput.sudosquadattendancesystem.domain;

import lombok.Data;

@Data
public class HardwarePayload {
        private String nodeId;
        private String rfidTag;
        private Integer fingerprintId;
}


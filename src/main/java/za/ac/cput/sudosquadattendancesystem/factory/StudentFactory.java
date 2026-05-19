package za.ac.cput.sudosquadattendancesystem.factory;
import za.ac.cput.sudosquadattendancesystem.domain.Student;

public class StudentFactory {

    public static Student build(String studentNumber,
                                String firstName,
                                String lastName,
                                String email,
                                String rfidTagId,
                                Integer fingerprintId) {

        if (studentNumber == null || studentNumber.isBlank()) return null;
        if (firstName == null || firstName.isBlank()) return null;
        if (lastName == null || lastName.isBlank()) return null;
        if (email == null || email.isBlank())  return null;

        return Student.builder()
                .studentNumber(studentNumber.trim().toUpperCase())
                .firstName(firstName.trim())
                .lastName(lastName.trim())
                .email(email.trim().toLowerCase())
                .rfidTagId(rfidTagId != null ? rfidTagId.trim().toUpperCase() : null)
                .fingerprintId(fingerprintId)
                .enrolled(true)
                .build();
    }
}


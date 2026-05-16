package za.ac.cput.sudosquadattendancesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.sudosquadattendancesystem.domain.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRfidTagId(String rfidTagId);
    Optional<Student> findByFingerprintId(Integer fingerprintId);

    boolean existsByRfidTagIdAndEnrolledTrue(String rfidTagId);
    boolean existsByFingerprintIdAndEnrolledTrue(Integer fingerprintId);

    Optional<Student> findByStudentNumber(String studentNumber);

    List<Student> findByEnrolledTrueOrderByLastNameAsc();

    boolean existsByEmail(String email);
    boolean existsByStudentNumber(String studentNumber);
}

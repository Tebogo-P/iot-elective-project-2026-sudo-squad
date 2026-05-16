package za.ac.cput.sudosquadattendancesystem.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.ac.cput.sudosquadattendancesystem.domain.Student;
import za.ac.cput.sudosquadattendancesystem.repository.StudentRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;

    @Override
    public Student create(Student student) {
        if (student == null) {
            log.warn("create() called with null student");
            return null;
        }
        if (studentRepository.existsByStudentNumber(student.getStudentNumber())) {
            log.warn("Student number '{}' already exists", student.getStudentNumber());
            return null;
        }
        if (studentRepository.existsByEmail(student.getEmail())) {
            log.warn("Email '{}' already registered", student.getEmail());
            return null;
        }
        Student saved = studentRepository.save(student);
        log.info("Student created: {} (id={})", saved.getStudentNumber(), saved.getId());
        return saved;
    }

    @Override
    public Student read(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student readByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber).orElse(null);
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getEnrolledStudents() {
        return studentRepository.findByEnrolledTrueOrderByLastNameAsc();
    }

    @Override
    public Student update(Student student) {
        if (student == null || student.getId() == null) return null;
        if (!studentRepository.existsById(student.getId()))  return null;
        Student updated = studentRepository.save(student);
        log.info("Student updated: {} (id={})", updated.getStudentNumber(), updated.getId());
        return updated;
    }

    @Override
    public Student unenroll(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) return null;
        student.setEnrolled(false);
        Student saved = studentRepository.save(student);
        log.info("Student unenrolled: {} (id={})", saved.getStudentNumber(), saved.getId());
        return saved;
    }

    @Override
    public Student enroll(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) return null;
        student.setEnrolled(true);
        Student saved = studentRepository.save(student);
        log.info("Student enrolled: {} (id={})", saved.getStudentNumber(), saved.getId());
        return saved;
    }


    @Override
    public void delete(Long id) {
        studentRepository.deleteById(id);
        log.info("Student {} permanently deleted", id);
    }
}

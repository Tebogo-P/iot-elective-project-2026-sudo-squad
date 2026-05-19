package za.ac.cput.sudosquadattendancesystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.sudosquadattendancesystem.domain.Student;
import za.ac.cput.sudosquadattendancesystem.factory.StudentFactory;
import za.ac.cput.sudosquadattendancesystem.service.IStudentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
@RequiredArgsConstructor
public class StudentController {

    private final IStudentService studentService;

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Map<String, Object> body) {
        Student student = StudentFactory.build(
                (String)  body.get("studentNumber"),
                (String)  body.get("firstName"),
                (String)  body.get("lastName"),
                (String)  body.get("email"),
                (String)  body.get("rfidTagId"),
                body.get("fingerprintId") != null ? (Integer) body.get("fingerprintId") : null
        );

        if (student == null) return ResponseEntity.badRequest().build();

        Student saved = studentService.create(student);
        if (saved == null) return ResponseEntity.status(409).build();; // duplicate number/email
        return ResponseEntity.ok(saved);
    }

    // GET /api/students/1
    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        Student student = studentService.read(id);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(student);
    }

    // GET /api/students/number/230226442
    @GetMapping("/number/{studentNumber}")
    public ResponseEntity<Student> getByStudentNumber(@PathVariable String studentNumber) {
        Student student = studentService.readByStudentNumber(studentNumber);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(student);
    }

    // GET /api/students           — all students including unenrolled
    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(studentService.getAll());
    }

    // GET /api/students/enrolled  — only active students (enrolled=true)
    @GetMapping("/enrolled")
    public ResponseEntity<List<Student>> getEnrolled() {
        return ResponseEntity.ok(studentService.getEnrolledStudents());
    }

    @PutMapping
    public ResponseEntity<Student> update(@RequestBody Student student) {
        Student updated = studentService.update(student);
        if (updated == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/unenroll")
    public ResponseEntity<Student> unenroll(@PathVariable Long id) {
        Student student = studentService.unenroll(id);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(student);
    }

    @PatchMapping("/{id}/enroll")
    public ResponseEntity<Student> enroll(@PathVariable Long id) {
        Student student = studentService.enroll(id);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(student);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

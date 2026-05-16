package za.ac.cput.sudosquadattendancesystem.service;


import za.ac.cput.sudosquadattendancesystem.domain.Student;

import java.util.List;

public interface IStudentService {

    Student     create(Student student);
    Student     read(Long id);
    Student     readByStudentNumber(String studentNumber);
    List<Student> getAll();
    List<Student> getEnrolledStudents();
    Student     update(Student student);

    Student     unenroll(Long id);

    Student     enroll(Long id);

    void delete(Long id);

}
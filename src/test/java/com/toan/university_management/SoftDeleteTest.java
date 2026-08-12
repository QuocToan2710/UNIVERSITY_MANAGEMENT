//package com.toan.university_management;
//
//import com.toan.university_management.entity.identity.User;
//import com.toan.university_management.entity.masterdata.Student;
//import com.toan.university_management.entity.masterdata.Subject;
//import com.toan.university_management.entity.masterdata.Teacher;
//import com.toan.university_management.repository.identity.UserRepository;
//import com.toan.university_management.repository.masterdata.StudentRepository;
//import com.toan.university_management.repository.masterdata.SubjectRepository;
//import com.toan.university_management.repository.masterdata.TeacherRepository;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ActiveProfiles("test")
//class SoftDeleteTest {
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Autowired
//    private TeacherRepository teacherRepository;
//
//    @Autowired
//    private SubjectRepository subjectRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Test
//    @DisplayName("Test Soft Delete for Student entity")
//    void testStudentSoftDelete() {
//        Student student = Student.builder()
//                .studentCode("SV001")
//                .fullName("Nguyen Van A")
//                .email("a@gmail.com")
//                .build();
//        Student saved = studentRepository.save(student);
//        String id = saved.getId
//                ();
//        assertNotNull(id);
//        assertFalse(saved.isDeleted());
//
//        studentRepository.deleteById(id);
//        entityManager.flush();
//        entityManager.clear();
//
//        assertTrue(studentRepository.findByIdAndDeletedFalse(id).isEmpty());
//        assertFalse(studentRepository.existsByIdAndDeletedFalse(id));
//
//        Object result = entityManager.createNativeQuery("SELECT deleted FROM student WHERE id = :id")
//                .setParameter("id", id)
//                .getSingleResult();
//        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
//    }
//
//    @Test
//    @DisplayName("Test Soft Delete for Teacher entity")
//    void testTeacherSoftDelete() {
//        Teacher teacher = Teacher.builder()
//                .teacherCode("GV001")
//                .fullName("Tran Van B")
//                .email("b@gmail.com")
//                .degree("Thạc sĩ")
//                .build();
//        Teacher saved = teacherRepository.save(teacher);
//        String id = saved.getId();
//
//        teacherRepository.deleteById(id);
//        entityManager.flush();
//        entityManager.clear();
//
//        assertTrue(teacherRepository.findByIdAndDeletedFalse(id).isEmpty());
//        Object result = entityManager.createNativeQuery("SELECT deleted FROM teacher WHERE id = :id")
//                .setParameter("id", id)
//                .getSingleResult();
//        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
//    }
//
//    @Test
//    @DisplayName("Test Soft Delete for Subject entity")
//    void testSubjectSoftDelete() {
//        Subject subject = Subject.builder()
//                .subjectCode("CS101")
//                .name("Lap trinh Java")
//                .credit(3)
//                .build();
//        Subject saved = subjectRepository.save(subject);
//        String id = saved.getId();
//
//        subjectRepository.deleteById(id);
//        entityManager.flush();
//        entityManager.clear();
//
//        assertTrue(subjectRepository.findByIdAndDeletedFalse(id).isEmpty());
//        Object result = entityManager.createNativeQuery("SELECT deleted FROM subject WHERE id = :id")
//                .setParameter("id", id)
//                .getSingleResult();
//        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
//    }
//
//    @Test
//    @DisplayName("Test Soft Delete for User entity")
//    void testUserSoftDelete() {
//        User user = User.builder()
//                .userCode("USR_TEST")
//                .username("testuser")
//                .password("password123")
//                .email("user@gmail.com")
//                .fullName("User Test")
//                .build();
//        User saved = userRepository.save(user);
//        String id = saved.getId();
//
//        userRepository.deleteById(id);
//        entityManager.flush();
//        entityManager.clear();
//
//        assertTrue(userRepository.findById(id).isEmpty());
//        Object result = entityManager.createNativeQuery("SELECT deleted FROM user WHERE id = :id")
//                .setParameter("id", id)
//                .getSingleResult();
//        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
//    }
//}

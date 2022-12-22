package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Value("${sql.scripts.create.student}")
    private String sqlCreateStudent;

    @Value("${sql.scripts.create.math.grade}")
    private String sqlCreateMathGrade;

    @Value("${sql.scripts.create.science.grade}")
    private String sqlCreateScienceGrade;

    @Value("${sql.scripts.create.history.grade}")
    private String sqlCreateHistoryGrade;

    @Value("${sql.scripts.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.scripts.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.scripts.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.scripts.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }

    @Test
    public void createStudent() {
        studentAndGradeService.createStudent("Chad", "Darby", "chad.darby@luv2code_school.com");

        CollegeStudent student = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");

        assertEquals("chad.darby@luv2code_school.com", student.getEmailAddress(), "find by email");
    }

    @Test
    public void checkIfStudentExists() {

        assertTrue(studentAndGradeService.checkIfStudentExists(1));

        assertFalse(studentAndGradeService.checkIfStudentExists(0));
    }

    @Test
    public void deleteStudent() {
        Optional<CollegeStudent> collegeStudent = studentDao.findById(1);
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> scienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> historyGrade = historyGradeDao.findById(1);

        assertTrue(collegeStudent.isPresent(), "Return true");
        assertTrue(mathGrade.isPresent(), "Return true");
        assertTrue(scienceGrade.isPresent(), "Return true");
        assertTrue(historyGrade.isPresent(), "Return true");

        studentAndGradeService.deleteStudent(1);

        collegeStudent = studentDao.findById(1);
        mathGrade = mathGradeDao.findById(1);
        scienceGrade = scienceGradeDao.findById(1);
        historyGrade = historyGradeDao.findById(1);

        assertFalse(collegeStudent.isPresent(), "Return false");
        assertFalse(mathGrade.isPresent(), "Return false");
        assertFalse(scienceGrade.isPresent(), "Return false");
        assertFalse(historyGrade.isPresent(), "Return false");
    }

    @Test
    @Sql("/insertData.sql")
    public void getGradebook() {
        Iterable<CollegeStudent> collegeStudentsIterable = studentAndGradeService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : collegeStudentsIterable) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGrade() {
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "math"));
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "science"));
        assertTrue(studentAndGradeService.createGrade(80.50, 1, "history"));

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradesByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradesByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradesByStudentId(1);

        assertEquals(2, ((Collection<MathGrade>) mathGrades).size(),"Student has math grades");
        assertEquals(2, ((Collection<ScienceGrade>) scienceGrades).size(),"Student has science grades");
        assertEquals(2, ((Collection<HistoryGrade>) historyGrades).size(), "Student has history grades");
    }

    @Test
    public void createGradeReturnFalse() {
        assertFalse(studentAndGradeService.createGrade(105, 1, "math"));
        assertFalse(studentAndGradeService.createGrade(-5, 1, "math"));
        assertFalse(studentAndGradeService.createGrade(80.50, 2, "math"));
        assertFalse(studentAndGradeService.createGrade(80.50, 1, "literature"));
    }

    @Test
    public void deleteGrade() {
        assertEquals(1, studentAndGradeService.deleteGrade(1, "math"),
                "Returns student id after delete");
        assertEquals(1, studentAndGradeService.deleteGrade(1, "science"),
                "Returns student id after delete");
        assertEquals(1, studentAndGradeService.deleteGrade(1, "history"),
                "Returns student id after delete");
    }

    @Test
    public void deleteGradeReturnStudentIdOfZero() {
        assertEquals(0, studentAndGradeService.deleteGrade(0, "science"),
                "No student should have id of 0");
        assertEquals(0, studentAndGradeService.deleteGrade(1, "literature"),
                "No student should have a literature class");
    }

    @Test
    public void getGradebookCollegeStudent() {
        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.getGradebookCollegeStudent(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("eric.roby@luv2code_school.com", gradebookCollegeStudent.getEmailAddress());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
    }

    @Test
    public void getGradebookCollegeStudentIdDoesNotExist() {
        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.getGradebookCollegeStudent(0);
        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void teardownDatabase() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
}

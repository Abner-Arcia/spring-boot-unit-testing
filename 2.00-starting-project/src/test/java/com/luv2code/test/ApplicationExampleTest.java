package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class ApplicationExampleTest {

    private static int count = 0;

    @Value("${info.app.name}")
    private String appInfo;

    @Value("${info.app.description}")
    private String appDescription;

    @Value("${info.app.version}")
    private String appVersion;

    @Autowired
    CollegeStudent student;

    @Autowired
    StudentGrades studentGrades;

    @Autowired
    ApplicationContext context;

    @BeforeEach
    public void beforeEach() {
        count++;
        System.out.println("Testing: " + appInfo + " which is " + appDescription + " Version: " + appVersion
                + ". Execution of test method " + count);
        student.setFirstname("Eric");
        student.setLastname("Roby");
        student.setEmailAddress("eric.roby@luv2code_school.com");
        studentGrades.setMathGradeResults(Arrays.asList(100.0, 85.0, 76.5, 91.75));
        student.setStudentGrades(studentGrades);
    }

    @Test
    @DisplayName("Add grade results for student grades")
    public void addGradeResultsForStudentGrades() {
        assertEquals(353.25, studentGrades.addGradeResultsForSingleClass(
                student.getStudentGrades().getMathGradeResults()
        ));
    }

    @Test
    @DisplayName("Add grade results for student grades not equal")
    public void addGradeResultsForStudentGradesNotEqual() {
        assertNotEquals(0, studentGrades.addGradeResultsForSingleClass(
                student.getStudentGrades().getMathGradeResults()
        ));
    }

    @Test
    @DisplayName("Is grade greater true")
    public void isGradeGreaterStudentGradesTrue() {
        assertTrue(studentGrades.isGradeGreater(90, 75), "failure - should be true");
    }

    @Test
    @DisplayName("Is grade greater false")
    public void isGradeGreaterStudentGradesFalse() {
        assertFalse(studentGrades.isGradeGreater(89, 92), "failure - should be false");
    }

    @Test
    @DisplayName("Check null for student grades")
    public void checkNullForStudentGrades() {
        assertNotNull(
                studentGrades.checkNull(student.getStudentGrades().getMathGradeResults()),
                "object should not be null"
        );
    }

    @Test
    @DisplayName("Create student without grade init")
    public void createStudentWithoutGradesInit() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
        studentTwo.setFirstname("Chad");
        studentTwo.setLastname("Darby");
        studentTwo.setEmailAddress("chad.darby@luv2code_school.com");
        assertNotNull(studentTwo.getFirstname());
        assertNotNull(studentTwo.getLastname());
        assertNotNull(studentTwo.getEmailAddress());
        assertNull(studentGrades.checkNull(studentTwo.getStudentGrades()));
    }

    @Test
    @DisplayName("Verify students are prototypes")
    public void verifyStudentsArePrototypes() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
        assertNotSame(student, studentTwo);
    }

    @Test
    @DisplayName("Find Grade Point Average")
    public void findGradePointAverage() {
        assertAll(
                "Testing all assertEquals",
                () -> assertEquals(
                        353.25,
                        studentGrades.addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults())
                ),
                () -> assertEquals(
                        88.31,
                        studentGrades.findGradePointAverage(student.getStudentGrades().getMathGradeResults())
                )
        );
    }
}

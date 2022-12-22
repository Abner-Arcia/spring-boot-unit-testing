package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Value("${sql.scripts.create.student}")
    private String sqlCreateStudent;

    @Value("${sql.scripts.create.math.grade}")
    private String sqlCreateMathGrade;

    @Value("${sql.scripts.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.scripts.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chad");
        request.setParameter("lastname", "Darby");
        request.setParameter("emailAddress", "chad.darby@luv2code_school.com");
    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstname", request.getParameterValues("firstname"))
                        .param("lastname", request.getParameterValues("lastname"))
                        .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent student = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");

        assertNotNull(student, "Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/delete/student/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    public void deleteStudentHttpRequestStudentDoesNotExist() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/delete/student/{id}", 0))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void studentInformationHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/studentInformation/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");
    }

    @Test
    public void studentInformationHttpRequestStudentDoesNotExist() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void createGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());

        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.getGradebookCollegeStudent(1);

        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/grades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("grade", "85.00")
                                .param("gradeType", "math")
                                .param("studentId", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");

        assertEquals(2, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
    }

    @Test
    public void createGradeHttpRequestStudentDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/grades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("grade", "85.00")
                                .param("gradeType", "history")
                                .param("studentId", "0"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void createGradeHttpRequestGradeTypeDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/grades")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("grade", "85.00")
                                .param("gradeType", "literature")
                                .param("studentId", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void deleteGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);

        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");

        mathGrade = mathGradeDao.findById(1);

        assertFalse(mathGrade.isPresent());
    }

    @Test
    public void deleteGradeHttpRequestStudentIdDoesNotExists() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(2);

        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 2, "math"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    public void deleteGradeHttpRequestGradeTypeDoesNotExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 1, "literature"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @AfterEach
    public void teardownDatabase() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
    }
}

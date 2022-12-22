package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstname, String lastname, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean checkIfStudentExists(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudent(int id) {
        if (checkIfStudentExists(id)) {
            studentDao.deleteById(id);
            mathGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
            historyGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        return studentDao.findAll();
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if (!checkIfStudentExists(studentId)) {
            return false;
        }

        if (grade >= 0 && grade <= 100) {
            switch (gradeType) {
                case "math" -> {
                    mathGrade.setId(0);
                    mathGrade.setGrade(grade);
                    mathGrade.setStudentId(studentId);
                    mathGradeDao.save(mathGrade);
                    return true;
                }
                case "science" -> {
                    scienceGrade.setId(0);
                    scienceGrade.setGrade(grade);
                    scienceGrade.setStudentId(studentId);
                    scienceGradeDao.save(scienceGrade);
                    return true;
                }
                case "history" -> {
                    historyGrade.setId(0);
                    historyGrade.setGrade(grade);
                    historyGrade.setStudentId(studentId);
                    historyGradeDao.save(historyGrade);
                    return true;
                }
            }
        }

        return false;
    }

    public int deleteGrade(int id, String gradeType) {
        int studentId = 0;

        switch (gradeType) {
            case "math" -> {
                Optional<MathGrade> grade = mathGradeDao.findById(id);
                if (grade.isPresent()) {
                    studentId = grade.get().getStudentId();
                    mathGradeDao.deleteById(id);
                }
            }
            case "science" -> {
                Optional<ScienceGrade> grade = scienceGradeDao.findById(id);
                if (grade.isPresent()) {
                    studentId = grade.get().getStudentId();
                    scienceGradeDao.deleteById(id);
                }
            }
            case "history" -> {
                Optional<HistoryGrade> grade = historyGradeDao.findById(id);
                if (grade.isPresent()) {
                    studentId = grade.get().getStudentId();
                    historyGradeDao.deleteById(id);
                }
            }
        }

        return studentId;
    }

    public GradebookCollegeStudent getGradebookCollegeStudent(int id) {

        if (!checkIfStudentExists(id)) {
            return null;
        }

        Optional<CollegeStudent> student = studentDao.findById(id);
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradesByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradesByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradesByStudentId(id);

        List<Grade> mathGradesList = new ArrayList<>();
        mathGrades.forEach(mathGradesList::add);
        List<Grade> scienceGradesList = new ArrayList<>();
        scienceGrades.forEach(scienceGradesList::add);
        List<Grade> historyGradesList = new ArrayList<>();
        historyGrades.forEach(historyGradesList::add);

        studentGrades.setMathGradeResults(mathGradesList);
        studentGrades.setScienceGradeResults(scienceGradesList);
        studentGrades.setHistoryGradeResults(historyGradesList);

        return new GradebookCollegeStudent(
                student.get().getId(),
                student.get().getFirstname(),
                student.get().getLastname(),
                student.get().getEmailAddress(),
                studentGrades
        );
    }

    public void configureStudentInformationModel(int id, Model m) {
        GradebookCollegeStudent gradebookCollegeStudent = getGradebookCollegeStudent(id);
        m.addAttribute("student", gradebookCollegeStudent);

        if (!gradebookCollegeStudent.getStudentGrades().getMathGradeResults().isEmpty()) {
            m.addAttribute("mathAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getMathGradeResults()
            ));
        } else {
            m.addAttribute("mathAverage", "N/A");
        }

        if (!gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().isEmpty()) {
            m.addAttribute("scienceAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getScienceGradeResults()
            ));
        } else {
            m.addAttribute("scienceAverage", "N/A");
        }

        if (!gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().isEmpty()) {
            m.addAttribute("historyAverage", gradebookCollegeStudent.getStudentGrades().findGradePointAverage(
                    gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults()
            ));
        } else {
            m.addAttribute("historyAverage", "N/A");
        }
    }
}

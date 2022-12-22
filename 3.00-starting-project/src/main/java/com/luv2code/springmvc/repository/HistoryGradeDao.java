package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradeDao extends CrudRepository<HistoryGrade, Integer> {

    Iterable<HistoryGrade> findGradesByStudentId(int id);

    void deleteByStudentId(int id);
}

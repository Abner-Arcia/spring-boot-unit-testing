package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradeDao extends CrudRepository<ScienceGrade, Integer> {

    Iterable<ScienceGrade> findGradesByStudentId(int id);

    void deleteByStudentId(int id);
}

package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, String> {

    boolean existsByClassCode(String classCode);

    Optional<ClassGroup> findByClassCode(String classCode);

    @Query("SELECT cg FROM ClassGroup cg LEFT JOIN FETCH cg.homeroomTeacher")
    List<ClassGroup> findAllWithTeacher();
}



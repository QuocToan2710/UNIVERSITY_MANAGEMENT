package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    List<Ward> findAllByDeletedFalseOrderByWardNameAsc();
    List<Ward> findAllByDistrictIdAndDeletedFalseOrderByWardNameAsc(Long districtId);
    List<Ward> findAllByIdInAndDeletedFalse(Set<Long> ids);
    Optional<Ward> findByIdAndDeletedFalse(Long id);
    Optional<Ward> findByWardCodeAndDeletedFalse(String wardCode);
    boolean existsByWardCodeAndDeletedFalse(String wardCode);
    boolean existsByIdAndDeletedFalse(Long id);
}

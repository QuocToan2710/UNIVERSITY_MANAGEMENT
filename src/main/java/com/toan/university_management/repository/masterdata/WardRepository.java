package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Ward;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends BaseRepository<Ward, Long> {
    List<Ward> findAllByDeletedFalseOrderByWardNameAsc();
    List<Ward> findAllByDistrictIdAndDeletedFalseOrderByWardNameAsc(Long districtId);
    Optional<Ward> findByWardCodeAndDeletedFalse(String wardCode);
    boolean existsByWardCodeAndDeletedFalse(String wardCode);
}

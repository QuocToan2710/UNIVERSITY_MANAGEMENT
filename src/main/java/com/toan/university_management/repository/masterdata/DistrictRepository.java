package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.District;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends BaseRepository<District, Long> {
    List<District> findAllByDeletedFalseOrderByDistrictNameAsc();
    List<District> findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(Long provinceId);
    Optional<District> findByDistrictCodeAndDeletedFalse(String districtCode);
    boolean existsByDistrictCodeAndDeletedFalse(String districtCode);
}

package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends BaseRepository<District, Long> {
    List<District> findAllByDeletedFalseOrderByDistrictNameAsc();
    List<District> findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(Long provinceId);
    Page<District> findAllByDeletedFalseOrderByDistrictNameAsc(Pageable pageable);
    Page<District> findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(Long provinceId, Pageable pageable);
    Optional<District> findByDistrictCodeAndDeletedFalse(String districtCode);
    boolean existsByDistrictCodeAndDeletedFalse(String districtCode);
}

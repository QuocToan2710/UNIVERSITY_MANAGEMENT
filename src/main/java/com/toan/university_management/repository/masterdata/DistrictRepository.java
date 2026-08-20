package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findAllByDeletedFalseOrderByDistrictNameAsc();
    List<District> findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(Long provinceId);
    List<District> findAllByIdInAndDeletedFalse(Set<Long> ids);
    Optional<District> findByIdAndDeletedFalse(Long id);
    Optional<District> findByDistrictCodeAndDeletedFalse(String districtCode);
    boolean existsByDistrictCodeAndDeletedFalse(String districtCode);
    boolean existsByIdAndDeletedFalse(Long id);
}

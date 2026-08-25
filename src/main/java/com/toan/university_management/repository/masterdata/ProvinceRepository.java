package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Province;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends BaseRepository<Province, Long> {
    List<Province> findAllByDeletedFalseOrderByProvinceNameAsc();
    Optional<Province> findByProvinceCodeAndDeletedFalse(String provinceCode);
    boolean existsByProvinceCodeAndDeletedFalse(String provinceCode);
}

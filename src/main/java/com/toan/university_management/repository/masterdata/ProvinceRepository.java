package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findAllByDeletedFalseOrderByProvinceNameAsc();
    List<Province> findAllByIdInAndDeletedFalse(Set<Long> ids);
    Optional<Province> findByIdAndDeletedFalse(Long id);
    Optional<Province> findByProvinceCodeAndDeletedFalse(String provinceCode);
    boolean existsByProvinceCodeAndDeletedFalse(String provinceCode);
    boolean existsByIdAndDeletedFalse(Long id);
}

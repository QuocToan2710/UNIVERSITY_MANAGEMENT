package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByIdAndDeletedFalse(Long id);
    Optional<Building> findByBuildingCodeAndDeletedFalse(String buildingCode);
    Page<Building> findAllByDeletedFalse(Pageable pageable);
    List<Building> findAllByDeletedFalse();
    boolean existsByBuildingCodeAndDeletedFalse(String buildingCode);
    boolean existsByIdAndDeletedFalse(Long id);
}

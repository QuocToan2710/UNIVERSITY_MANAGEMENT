package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Building;

import java.util.Optional;

public interface BuildingRepository extends BaseRepository<Building, Long> {
    Optional<Building> findByBuildingCodeAndDeletedFalse(String buildingCode);
    boolean existsByBuildingCodeAndDeletedFalse(String buildingCode);
}

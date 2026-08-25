package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Floor;

import java.util.Optional;

public interface FloorRepository extends BaseRepository<Floor, Long> {
    Optional<Floor> findByFloorCodeAndDeletedFalse(String floorCode);
    boolean existsByFloorCodeAndDeletedFalse(String floorCode);
}

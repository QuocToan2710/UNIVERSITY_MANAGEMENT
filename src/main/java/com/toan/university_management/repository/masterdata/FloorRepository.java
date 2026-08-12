package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    Optional<Floor> findByIdAndDeletedFalse(Long id);
    Page<Floor> findAllByDeletedFalse(Pageable pageable);
    List<Floor> findAllByDeletedFalse();
    boolean existsByFloorCodeAndDeletedFalse(String floorCode);
    boolean existsByIdAndDeletedFalse(Long id);
}

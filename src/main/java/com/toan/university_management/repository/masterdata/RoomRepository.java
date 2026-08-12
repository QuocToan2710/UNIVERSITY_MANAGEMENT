package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIdAndDeletedFalse(Long id);
    Page<Room> findAllByDeletedFalse(Pageable pageable);
    List<Room> findAllByDeletedFalse();
    boolean existsByRoomCodeAndDeletedFalse(String roomCode);
    boolean existsByIdAndDeletedFalse(Long id);
}

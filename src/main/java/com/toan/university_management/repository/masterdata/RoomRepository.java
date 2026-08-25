package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Room;

import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room, Long> {
    Optional<Room> findByRoomCodeAndDeletedFalse(String roomCode);
    boolean existsByRoomCodeAndDeletedFalse(String roomCode);
}

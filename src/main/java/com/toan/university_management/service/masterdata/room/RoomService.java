package com.toan.university_management.service.masterdata.room;

import com.toan.university_management.dto.request.masterdata.RoomRequest;
import com.toan.university_management.dto.response.masterdata.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    RoomResponse getRoomById(Long id);
    List<RoomResponse> getAllRooms();
    Page<RoomResponse> getAllRooms(Pageable pageable);
    RoomResponse updateRoom(Long id, RoomRequest request);
    void deleteRoom(Long id);
}

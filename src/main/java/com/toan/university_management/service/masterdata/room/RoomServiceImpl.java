package com.toan.university_management.service.masterdata.room;

import com.toan.university_management.dto.request.masterdata.RoomRequest;
import com.toan.university_management.dto.response.masterdata.RoomResponse;
import com.toan.university_management.entity.masterdata.Room;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.RoomMapper;
import com.toan.university_management.repository.masterdata.BuildingRepository;
import com.toan.university_management.repository.masterdata.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class RoomServiceImpl implements RoomService {
    RoomRepository roomRepository;
    BuildingRepository buildingRepository;
    RoomMapper roomMapper;

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomCodeAndDeletedFalse(request.getRoomCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getBuildingId() != null && !buildingRepository.existsByIdAndDeletedFalse(request.getBuildingId())) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        Room room = roomMapper.toRoom(request);

        if (room.getBuildingId() != null && (room.getBuilding() == null || room.getBuilding().isBlank())) {
            buildingRepository.findByIdAndDeletedFalse(room.getBuildingId())
                    .ifPresent(b -> room.setBuilding(b.getName()));
        }

        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus("ACTIVE");
        }
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toRoomResponse(savedRoom);
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAllByDeletedFalse().stream()
                .map(roomMapper::toRoomResponse)
                .toList();
    }

    @Override
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findAllByDeletedFalse(pageable)
                .map(roomMapper::toRoomResponse);
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if (request.getBuildingId() != null && !buildingRepository.existsByIdAndDeletedFalse(request.getBuildingId())) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        roomMapper.updateRoom(room, request);
        room = roomRepository.save(room);
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }
        roomRepository.deleteById(id);
    }
}

package com.toan.university_management.enums;

import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RoomType {
    LECTURE_HALL("Giảng đường", "Giảng đường", "LECTURE_HALL"),
    COMPUTER_LAB("Phòng máy tính", "Phòng máy tính", "COMPUTER_LAB"),
    LAB("Phòng thí nghiệm", "Phòng thí nghiệm", "LAB"),
    AUDITORIUM("Hội trường", "Hội trường", "AUDITORIUM"),
    PRACTICE_ROOM("Phòng thực hành", "Phòng thực hành", "PRACTICE_ROOM");

    String value;
    String label;
    String code;

    public SelectOptionResponse toSelectOption() {
        return new SelectOptionResponse(value, label, code, null);
    }
}

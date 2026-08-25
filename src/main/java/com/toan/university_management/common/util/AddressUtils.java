package com.toan.university_management.common.util;

import java.util.ArrayList;
import java.util.List;

public final class AddressUtils {

    private AddressUtils() {
        // Prevent instantiation
    }

    /**
     * Ghép địa chỉ đầy đủ từ các thành phần: địa chỉ chi tiết, phường/xã, quận/huyện, tỉnh/thành phố.
     */
    public static String buildFullAddress(String specificAddress, String wardName, String districtName, String provinceName, String fallbackAddress) {
        List<String> parts = new ArrayList<>();
        if (specificAddress != null && !specificAddress.isBlank()) parts.add(specificAddress.trim());
        if (wardName != null && !wardName.isBlank()) parts.add(wardName.trim());
        if (districtName != null && !districtName.isBlank()) parts.add(districtName.trim());
        if (provinceName != null && !provinceName.isBlank()) parts.add(provinceName.trim());
        if (!parts.isEmpty()) {
            return String.join(", ", parts);
        }
        return fallbackAddress != null ? fallbackAddress.trim() : "";
    }
}

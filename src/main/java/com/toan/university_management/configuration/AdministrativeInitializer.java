package com.toan.university_management.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toan.university_management.entity.masterdata.District;
import com.toan.university_management.entity.masterdata.Province;
import com.toan.university_management.entity.masterdata.Ward;
import com.toan.university_management.repository.masterdata.DistrictRepository;
import com.toan.university_management.repository.masterdata.ProvinceRepository;
import com.toan.university_management.repository.masterdata.WardRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdministrativeInitializer {

    ProvinceRepository provinceRepository;
    DistrictRepository districtRepository;
    WardRepository wardRepository;
    ObjectMapper objectMapper;

    @Transactional
    public void initAdministrativeData() {
        long currentProvinces = provinceRepository.count();

        // Check if data is already initialized with post-merger dataset
        if (currentProvinces > 0 && wardRepository.count() > 3000) {
            log.info("Post-merger administrative master data already initialized.");
            return;
        }

        log.info("Initializing Vietnam Post-Merger Administrative Units Master Data...");

        try {
            ClassPathResource resource = new ClassPathResource("administrative_v2_full.json");
            if (!resource.exists()) {
                resource = new ClassPathResource("administrative_full.json");
            }

            if (!resource.exists()) {
                log.warn("Administrative dataset JSON file not found on classpath!");
                return;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                JsonNode rootNode = objectMapper.readTree(inputStream);

                if (!rootNode.isArray()) {
                    log.error("Expected JSON array in administrative dataset");
                    return;
                }

                // Clear previous dummy / partial data
                wardRepository.deleteAllInBatch();
                districtRepository.deleteAllInBatch();
                provinceRepository.deleteAllInBatch();

                List<Province> provincesToSave = new ArrayList<>();
                Map<Integer, JsonNode> wardsOrDistrictsMap = new LinkedHashMap<>();

                for (JsonNode provNode : rootNode) {
                    int pCode = provNode.path("code").asInt();
                    String pCodeStr = String.format("%02d", pCode);
                    String pName = provNode.path("name").asText();
                    String pTypeRaw = provNode.path("division_type").asText();
                    String pType = formatDivisionType(pTypeRaw, "Tỉnh");

                    Province province = Province.builder()
                            .provinceCode(pCodeStr)
                            .provinceName(pName)
                            .provinceType(pType)
                            .deleted(false)
                            .build();

                    provincesToSave.add(province);
                    // Check if v2 has direct wards array or v1 has districts array
                    if (provNode.has("wards")) {
                        wardsOrDistrictsMap.put(pCode, provNode.path("wards"));
                    } else if (provNode.has("districts")) {
                        wardsOrDistrictsMap.put(pCode, provNode.path("districts"));
                    }
                }

                List<Province> savedProvinces = provinceRepository.saveAll(provincesToSave);
                Map<String, Long> provinceCodeToIdMap = new HashMap<>();
                for (Province p : savedProvinces) {
                    provinceCodeToIdMap.put(p.getProvinceCode(), p.getId());
                }

                log.info("Saved {} post-merger provinces/cities.", savedProvinces.size());

                // Now populate districts and wards
                List<District> districtsToSave = new ArrayList<>();
                Map<Integer, List<JsonNode>> distCodeToWardsMap = new LinkedHashMap<>();

                for (Map.Entry<Integer, JsonNode> entry : wardsOrDistrictsMap.entrySet()) {
                    int pCode = entry.getKey();
                    String pCodeStr = String.format("%02d", pCode);
                    Long provId = provinceCodeToIdMap.get(pCodeStr);
                    if (provId == null) continue;

                    JsonNode childArray = entry.getValue();
                    if (childArray != null && childArray.isArray() && childArray.size() > 0) {
                        JsonNode firstElem = childArray.get(0);
                        if (firstElem.has("wards")) {
                            // 3-tier structure
                            for (JsonNode distNode : childArray) {
                                int dCode = distNode.path("code").asInt();
                                String dCodeStr = String.format("%03d", dCode);
                                String dName = distNode.path("name").asText();
                                String dType = formatDivisionType(distNode.path("division_type").asText(), "Quận/Huyện");

                                District district = District.builder()
                                        .districtCode(dCodeStr)
                                        .districtName(dName)
                                        .districtType(dType)
                                        .provinceId(provId)
                                        .deleted(false)
                                        .build();

                                districtsToSave.add(district);
                                List<JsonNode> wList = new ArrayList<>();
                                for (JsonNode w : distNode.path("wards")) {
                                    wList.add(w);
                                }
                                distCodeToWardsMap.put(dCode, wList);
                            }
                        } else {
                            // 2-tier post-merger structure (Province -> Wards directly)
                            // Create a consolidated district unit for the province
                            Province matchedProv = savedProvinces.stream()
                                    .filter(p -> p.getId().equals(provId))
                                    .findFirst()
                                    .orElse(null);
                            String dName = matchedProv != null ? matchedProv.getProvinceName() : ("Khu vực " + pCodeStr);
                            String dType = matchedProv != null && matchedProv.getProvinceType().contains("Thành phố") ? "Khu vực đô thị" : "Khu vực hành chính";

                            District district = District.builder()
                                    .districtCode(pCodeStr + "0")
                                    .districtName(dName)
                                    .districtType(dType)
                                    .provinceId(provId)
                                    .deleted(false)
                                    .build();

                            districtsToSave.add(district);
                            List<JsonNode> wList = new ArrayList<>();
                            for (JsonNode w : childArray) {
                                wList.add(w);
                            }
                            distCodeToWardsMap.put(pCode * 10, wList);
                        }
                    }
                }

                List<District> savedDistricts = districtRepository.saveAll(districtsToSave);
                Map<String, Long> districtCodeToIdMap = new HashMap<>();
                for (District d : savedDistricts) {
                    districtCodeToIdMap.put(d.getDistrictCode(), d.getId());
                }

                log.info("Saved {} districts. Now saving wards...", savedDistricts.size());

                List<Ward> wardsToSave = new ArrayList<>();
                for (Map.Entry<Integer, List<JsonNode>> entry : distCodeToWardsMap.entrySet()) {
                    int dKey = entry.getKey();
                    String dCodeStr = dKey < 1000 ? String.format("%03d", dKey) : String.valueOf(dKey);
                    Long distId = districtCodeToIdMap.get(dCodeStr);
                    if (distId == null && dKey % 10 == 0) {
                        // For 2-tier, format as %02d + "0" -> %03d
                        String altCode = String.format("%03d", dKey);
                        distId = districtCodeToIdMap.get(altCode);
                    }
                    if (distId == null) continue;

                    for (JsonNode wNode : entry.getValue()) {
                        int wCode = wNode.path("code").asInt();
                        String wCodeStr = String.format("%05d", wCode);
                        String wName = wNode.path("name").asText();
                        String wTypeRaw = wNode.path("division_type").asText();
                        String wType = formatDivisionType(wTypeRaw, "Phường/Xã");

                        Ward ward = Ward.builder()
                                .wardCode(wCodeStr)
                                .wardName(wName)
                                .wardType(wType)
                                .districtId(distId)
                                .deleted(false)
                                .build();

                        wardsToSave.add(ward);
                    }
                }

                // Batch save wards in chunks of 1500
                int chunkSize = 1500;
                for (int i = 0; i < wardsToSave.size(); i += chunkSize) {
                    int end = Math.min(i + chunkSize, wardsToSave.size());
                    wardRepository.saveAll(wardsToSave.subList(i, end));
                }

                log.info("Successfully populated Post-Merger Vietnam Administrative database: {} Provinces/Cities, {} Districts, {} Wards.",
                        savedProvinces.size(), savedDistricts.size(), wardsToSave.size());
            }
        } catch (Exception e) {
            log.error("Failed to populate administrative database: {}", e.getMessage(), e);
        }
    }

    private String formatDivisionType(String raw, String defaultType) {
        if (raw == null || raw.trim().isEmpty()) return defaultType;
        String lower = raw.trim().toLowerCase();
        return switch (lower) {
            case "thành phố trung ương" -> "Thành phố Trung ương";
            case "tỉnh" -> "Tỉnh";
            case "quận" -> "Quận";
            case "huyện" -> "Huyện";
            case "thị xã" -> "Thị xã";
            case "thành phố" -> "Thành phố";
            case "phường" -> "Phường";
            case "xã" -> "Xã";
            case "thị trấn" -> "Thị trấn";
            default -> Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
        };
    }
}

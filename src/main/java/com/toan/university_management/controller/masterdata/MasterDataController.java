package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.GetComboDataSourceInput;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;
import com.toan.university_management.enums.ComboType;
import com.toan.university_management.service.masterdata.MasterDataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/master-data")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MasterDataController {
    MasterDataService masterDataService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SelectOptionResponse>>> getDataByType(
            @RequestParam("type") ComboType type,
            @RequestParam(value = "cascader", required = false) String cascader,
            @RequestParam(value = "codeSystem", required = false) String codeSystem,
            @RequestParam(value = "isCodeIsId", required = false, defaultValue = "false") Boolean isCodeIsId
    ) {
        log.info("Fetching masterdata options for type={}, cascader={}, codeSystem={}, isCodeIsId={}", type, cascader, codeSystem, isCodeIsId);

        GetComboDataSourceInput input = new GetComboDataSourceInput()
                .setType(type)
                .setCascader(cascader)
                .setCodeSystem(codeSystem)
                .setIsCodeIsId(isCodeIsId);

        List<SelectOptionResponse> options = masterDataService.getByType(input);

        return ResponseEntity.ok(ApiResponse.<List<SelectOptionResponse>>builder()
                .code(1000)
                .message("Successfully fetched masterdata combo options for " + type)
                .result(options)
                .build());
    }
}

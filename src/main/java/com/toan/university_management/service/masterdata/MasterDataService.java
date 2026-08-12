package com.toan.university_management.service.masterdata;

import com.toan.university_management.dto.request.masterdata.GetComboDataSourceInput;
import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;

import java.util.List;

public interface MasterDataService {
    List<SelectOptionResponse> getByType(GetComboDataSourceInput input);
}

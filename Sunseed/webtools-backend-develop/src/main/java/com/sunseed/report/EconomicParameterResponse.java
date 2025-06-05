package com.sunseed.report;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunseed.entity.Currency;
import com.sunseed.entity.EconomicMultiCrop;
import com.sunseed.model.responseDTO.CropDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EconomicParameterResponse {
    private Long economicId;
    private Long runId;
    private Long cloneId;
    private Boolean isMaster;

    @JsonProperty("economicParameter")
    private boolean economicParameter;

    private List<CropDto> cropDtoSet;
    private Currency currency;

    private List<EconomicMultiCropResponse> economicMultiCropResponseList;

    private Double[] hourlySellingRates;

    private Instant createdAt;
    private Instant updatedAt;

}


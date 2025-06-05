package com.sunseed.model.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EconomicParametersResponseDto {
    private Long economicId;
    private Long runId;
    private Long cloneId;
    private Boolean isMaster;

    @JsonProperty("economicParameter")
    private boolean economicParameter;

    private List<CropDto> cropDtoSet;
    private CurrencyResponse currency;

    private List<EconomicMultiCropResponse> economicMultiCropResponseList;

    private Double[] hourlySellingRates;

    private Instant createdAt;
    private Instant updatedAt;

}

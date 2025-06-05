package com.sunseed.model.requestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.sunseed.model.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EconomicParametersRequestDto {
    @JsonProperty("economicParameter")
    private boolean economicParameter;

    private Long currencyId;
    private List<@Valid EconomicMultiCropRequestDto> economicMultiCrop;
//    @Positive(message = "value.not.negative")
//    private Integer minSellingPointOfPower;
//    @Positive(message = "value.not.negative")
//    private Integer maxSellingPointOfPower;

    private Double[] hourlySellingRates;
}

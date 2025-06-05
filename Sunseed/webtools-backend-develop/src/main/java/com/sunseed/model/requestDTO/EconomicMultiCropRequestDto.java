package com.sunseed.model.requestDTO;

import com.sunseed.model.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EconomicMultiCropRequestDto {
    private Long id;
    private Long cropId;
    @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double minReferenceYieldCost;
    @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
    @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double maxReferenceYieldCost;
    @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
    @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double minInputCostOfCrop;
    @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
    @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double maxInputCostOfCrop;
    @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
    @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double minSellingCostOfCrop;
    @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
    @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double maxSellingCostOfCrop;
   // @NotNull(message = "value.not.null", groups = ValidationGroups.EconomicParameterGroup.class)
   // @Positive(message = "value.not.negative", groups = ValidationGroups.EconomicParameterGroup.class)
    private Double cultivationArea;

}
